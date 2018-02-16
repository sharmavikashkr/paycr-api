package com.paycr.invoice.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.paycr.common.awss3.AwsS3Folder;
import com.paycr.common.awss3.AwsS3Service;
import com.paycr.common.bean.ChildInvoiceRequest;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.domain.BulkFlag;
import com.paycr.common.data.domain.BulkInvoiceUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.BulkFlagRepository;
import com.paycr.common.data.repository.BulkInvoiceUploadRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.util.CommonUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.paycr.invoice.validation.InvoiceValidator;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class CreateInvoiceService {

	private static final Logger logger = LoggerFactory.getLogger(CreateInvoiceService.class);

	private int maxUploadSizeInMb = 5 * 1024 * 1024;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private InvoiceService invSer;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private ConsumerDao conDao;

	@Autowired
	private NotifyService<InvoiceNotify> invNotSer;

	@Autowired
	private InvoiceValidator invValidator;

	@Autowired
	private Server server;

	@Autowired
	private AwsS3Service awsS3Ser;

	@Autowired
	private BulkInvoiceUploadRepository bulkUpdRepo;

	@Autowired
	private BulkFlagRepository bulkFlagRepo;

	@Autowired
	private TimelineService tlService;

	public Invoice single(Invoice invoice) {
		logger.info("Single Invoice request : {}", new Gson().toJson(invoice));
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		invoice.setMerchant(merchant);
		if (invoice.isUpdate()) {
			invoice.setUpdatedBy(user.getEmail());
		} else {
			invoice.setCreatedBy(user.getEmail());
		}
		invValidator.validate(invoice);
		invRepo.save(invoice);
		if (!invoice.isUpdate()) {
			MerchantPricing merPri = invoice.getMerchantPricing();
			merPri.setUseCount(merPri.getUseCount() + 1);
			merPriRepo.save(merPri);
		}
		if (invoice.isUpdate()) {
			tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice updated", true, user.getEmail());
		} else {
			tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice created", true, user.getEmail());
		}
		return invoice;
	}

	@Transactional
	public Invoice createChild(String invoiceCode, ChildInvoiceRequest chldInvReq, String createdBy) {
		logger.info("Child Invoice request : {}", invoiceCode);
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice) || !InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Invoice");
		}
		if (InvoiceType.BULK.equals(chldInvReq.getInvoiceType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "InvoiceType BULK not supported for child invoices");
		}
		Invoice childInvoice = invHelp.prepareChildInvoice(invoiceCode, chldInvReq.getInvoiceType(), createdBy);
		Consumer consumer = chldInvReq.getConsumer();
		consumer.setCreatedBy(createdBy);
		invHelp.updateConsumer(childInvoice, consumer);
		if (InvoiceType.RECURRING.equals(chldInvReq.getInvoiceType())) {
			RecurringInvoice recReq = chldInvReq.getRecInv();
			RecurringInvoice recInv = new RecurringInvoice();
			recInv.setRecurr(recReq.getRecurr());
			recInv.setTotal(recReq.getTotal());
			recInv.setStartDate(recReq.getStartDate());
			invSer.recurr(childInvoice.getInvoiceCode(), recInv, createdBy);
		} else {
			InvoiceSetting invSetting = childInvoice.getMerchant().getInvoiceSetting();
			InvoiceNotify invNot = new InvoiceNotify();
			invNot.setCreated(new Date());
			invNot.setInvoice(childInvoice);
			invNot.setCcMe(invSetting.isCcMe());
			invNot.setCcEmail(childInvoice.getCreatedBy());
			invNot.setEmailNote(invSetting.getEmailNote());
			invNot.setEmailSubject(invSetting.getEmailSubject());
			invNot.setEmailPdf(invSetting.isEmailPdf());
			invNot.setSendEmail(invSetting.isSendEmail());
			invNot.setSendSms(invSetting.isSendSms());
			invNotSer.notify(invNot);
			List<InvoiceNotify> invNots = new ArrayList<>();
			invNots.add(invNot);
			childInvoice.setNotices(invNots);
			childInvoice.setStatus(InvoiceStatus.UNPAID);
			invRepo.save(childInvoice);
			tlService.saveToTimeline(childInvoice.getId(), ObjectType.INVOICE, "Notification sent to consumer", true,
					createdBy);
		}
		return childInvoice;
	}

	@Async
	@Transactional
	public void uploadConsumers(String invoiceCode, MultipartFile consumerFile, String createdBy) throws IOException {
		logger.info("Upload Invoice request : {}", invoiceCode);
		if (maxUploadSizeInMb < consumerFile.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 5MBs");
		}
		List<BulkInvoiceUpload> bulkUploads = bulkUpdRepo.findByInvoiceCode(invoiceCode);
		Invoice parenInv = invRepo.findByInvoiceCode(invoiceCode);
		String fileName = invoiceCode + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkInvoiceLocation() + fileName;
		CSVWriter writer = new CSVWriter(new FileWriter(updatedCsv, true));
		Reader reader = new InputStreamReader(consumerFile.getInputStream());
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> consumerList = csvReader.readAll();
		csvReader.close();
		if (CommonUtil.isEmpty(consumerList) || consumerList.size() > 200) {
			String[] record = new String[1];
			record[0] = "Min 1 and Max 200 consumers can be uploaded";
			writer.writeNext(record);
		}
		for (String[] consumer : consumerList) {
			String[] record = new String[consumer.length + 1];
			for (int i = 0; i < consumer.length; i++) {
				record[i] = consumer[i];
			}
			String reason = "Invalid format";
			if (consumer.length == 3) {
				Consumer con = new Consumer();
				con.setName(consumer[0].trim());
				con.setEmail(consumer[1].trim());
				con.setMobile(consumer[2].trim());
				try {
					ChildInvoiceRequest chldInvReq = new ChildInvoiceRequest();
					chldInvReq.setConsumer(con);
					chldInvReq.setInvoiceType(InvoiceType.SINGLE);
					Invoice invoice = createChild(invoiceCode, chldInvReq, createdBy);
					reason = invoice.getInvoiceCode();
				} catch (PaycrException ex) {
					reason = ex.getMessage();
				} catch (Exception ex) {
					reason = "Something went wrong";
				}
			}
			record[consumer.length] = reason;
			writer.writeNext(record);
		}
		writer.close();
		awsS3Ser.saveFile(AwsS3Folder.INVOICE, new File(updatedCsv));
		Date timeNow = new Date();
		BulkInvoiceUpload bun = new BulkInvoiceUpload();
		bun.setCreated(timeNow);
		bun.setFileName(fileName);
		bun.setInvoiceCode(invoiceCode);
		bun.setCreatedBy(createdBy);
		bulkUpdRepo.save(bun);
		tlService.saveToTimeline(parenInv.getId(), ObjectType.INVOICE, "Consumers csv uploaded", true, createdBy);
	}

	@Async
	@Transactional
	public void createFlag(String invoiceCode, ChildInvoiceRequest chldInvReq, String createdBy,
			Merchant merchant) {
		logger.info("Create flag Invoices request : {}", invoiceCode);
		Invoice parenInv = invRepo.findByInvoiceCode(invoiceCode);
		Date timeNow = new Date();
		BulkFlag buf = new BulkFlag();
		buf.setCreated(timeNow);
		buf.setInvoiceType(chldInvReq.getInvoiceType());
		buf.setInvoiceCode(invoiceCode);
		buf.setCreatedBy(createdBy);
		if (CommonUtil.isEmpty(chldInvReq.getFlagList())) {
			buf.setFlags("");
			buf.setMessage("FAILURE : Empty flag filter");
		} else {
			SearchConsumerRequest searchReq = new SearchConsumerRequest();
			searchReq.setFlagList(chldInvReq.getFlagList());
			Set<Consumer> consumerSet = conDao.findConsumers(searchReq, merchant);
			for (Consumer consumer : consumerSet) {
				ChildInvoiceRequest chldInvReqNew = new ChildInvoiceRequest();
				chldInvReqNew.setConsumer(consumer);
				chldInvReqNew.setInvoiceType(chldInvReq.getInvoiceType());
				chldInvReqNew.setFlagList(chldInvReq.getFlagList());
				chldInvReqNew.setRecInv(chldInvReq.getRecInv());
				createChild(invoiceCode, chldInvReqNew, createdBy);
			}
			StringBuilder sb = new StringBuilder("");
			for (ConsumerFlag flag : chldInvReq.getFlagList()) {
				sb.append(flag.getName() + ", ");
			}
			buf.setFlags(sb.toString());
			buf.setMessage("SUCCESS");
			tlService.saveToTimeline(parenInv.getId(), ObjectType.INVOICE, "Child invoices created for flags",
					true, createdBy);
		}
		bulkFlagRepo.save(buf);
	}

}
