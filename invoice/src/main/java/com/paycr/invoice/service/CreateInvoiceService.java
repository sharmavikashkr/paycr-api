package com.paycr.invoice.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.ChildInvoiceRequest;
import com.paycr.common.bean.SearchConsumerRequest;
import com.paycr.common.bean.Server;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.domain.BulkCategory;
import com.paycr.common.data.domain.BulkInvoiceUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.BulkCategoryRepository;
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
import com.paycr.common.util.Constants;
import com.paycr.invoice.helper.InvoiceHelper;
import com.paycr.invoice.validation.InvoiceValidator;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class CreateInvoiceService {
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
	private BulkInvoiceUploadRepository bulkUpdRepo;

	@Autowired
	private BulkCategoryRepository bulkCatRepo;

	@Autowired
	private TimelineService tlService;

	public Invoice single(Invoice invoice) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		invoice.setMerchant(merchant);
		invoice.setCreatedBy(user.getEmail());
		if (invoice.isUpdate()) {
			invoice.setUpdatedBy(user.getEmail());
		}
		invValidator.validate(invoice);
		invRepo.save(invoice);
		if (!invoice.isUpdate()) {
			MerchantPricing merPri = invoice.getMerchantPricing();
			merPri.setInvCount(merPri.getInvCount() + 1);
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
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice) || !InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Invoice");
		}
		if (InvoiceType.BULK.equals(chldInvReq.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "InvoiceType BULK not supported for child invoices");
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
	public void uploadConsumers(String invoiceCode, MultipartFile consumers, String createdBy) throws IOException {
		List<BulkInvoiceUpload> bulkUploads = bulkUpdRepo.findByInvoiceCode(invoiceCode);
		Invoice parenInv = invRepo.findByInvoiceCode(invoiceCode);
		String fileName = invoiceCode + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkInvoiceLocation() + fileName;
		CSVWriter writer = new CSVWriter(new FileWriter(updatedCsv, true));
		Reader reader = new InputStreamReader(consumers.getInputStream());
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> consumerList = csvReader.readAll();
		csvReader.close();
		if (consumerList == null || consumerList.isEmpty() || consumerList.size() > 200) {
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
	public void createCategory(String invoiceCode, ChildInvoiceRequest chldInvReq, String createdBy,
			Merchant merchant) {
		Invoice parenInv = invRepo.findByInvoiceCode(invoiceCode);
		Date timeNow = new Date();
		BulkCategory buc = new BulkCategory();
		buc.setCreated(timeNow);
		buc.setInvoiceType(chldInvReq.getInvoiceType());
		buc.setInvoiceCode(invoiceCode);
		buc.setCreatedBy(createdBy);
		if (chldInvReq.getConCatList() == null || chldInvReq.getConCatList().isEmpty()) {
			buc.setCategories("");
			buc.setMessage("FAILURE : Empty category filter");
		} else {
			SearchConsumerRequest searchReq = new SearchConsumerRequest();
			searchReq.setConCatList(chldInvReq.getConCatList());
			Set<Consumer> consumerSet = conDao.findConsumers(searchReq, merchant);
			for (Consumer consumer : consumerSet) {
				ChildInvoiceRequest chldInvReqNew = new ChildInvoiceRequest();
				chldInvReqNew.setConsumer(consumer);
				chldInvReqNew.setInvoiceType(chldInvReq.getInvoiceType());
				chldInvReqNew.setConCatList(chldInvReq.getConCatList());
				chldInvReqNew.setRecInv(chldInvReq.getRecInv());
				createChild(invoiceCode, chldInvReqNew, createdBy);
			}
			StringBuilder sb = new StringBuilder("");
			for (ConsumerCategory conCat : chldInvReq.getConCatList()) {
				sb.append(conCat.getName() + " : " + conCat.getValue() + ", ");
			}
			buc.setCategories(sb.toString());
			buc.setMessage("SUCCESS");
			tlService.saveToTimeline(parenInv.getId(), ObjectType.INVOICE, "Child invoices created for categories",
					true, createdBy);
		}
		bulkCatRepo.save(buc);
	}

}
