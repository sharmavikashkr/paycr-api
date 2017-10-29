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
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.domain.BulkCategory;
import com.paycr.common.data.domain.BulkUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Timeline;
import com.paycr.common.data.repository.BulkCategoryRepository;
import com.paycr.common.data.repository.BulkUploadRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.TimelineRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.NotifyService;
import com.paycr.common.service.SecurityService;
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
	private NotifyService notSer;

	@Autowired
	private InvoiceValidator invValidator;

	@Autowired
	private Server server;

	@Autowired
	private BulkUploadRepository bulkUpdRepo;

	@Autowired
	private BulkCategoryRepository bulkCatRepo;

	@Autowired
	private TimelineRepository tlRepo;

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
		Timeline tl = new Timeline();
		tl.setCreatedBy(user.getEmail());
		tl.setCreated(invoice.getCreated());
		tl.setInternal(true);
		if (invoice.isUpdate()) {
			tl.setMessage("Invoice Updated");
		} else {
			tl.setMessage("Invoice Created");
		}
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
		return invoice;
	}

	@Transactional
	public Invoice createChild(String invoiceCode, ChildInvoiceRequest chldInvReq, String createdBy) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice) || !InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Invoice");
		}
		Date timeNow = new Date();
		if (InvoiceType.BULK.equals(chldInvReq.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "InvoiceType BULK not supported for child invoices");
		}
		Invoice childInvoice = invHelp.prepareChildInvoice(invoiceCode, chldInvReq.getInvoiceType(), createdBy);
		Consumer consumer = chldInvReq.getConsumer();
		consumer.setCreatedBy(createdBy);
		invHelp.updateConsumer(childInvoice, consumer);
		if (InvoiceType.RECURRING.equals(chldInvReq.getInvoiceType())) {
			invSer.recurr(childInvoice.getInvoiceCode(), chldInvReq.getRecInv(), createdBy);
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
			notSer.notify(childInvoice, invNot);
			List<InvoiceNotify> invNots = new ArrayList<>();
			invNots.add(invNot);
			childInvoice.setInvoiceNotices(invNots);
			childInvoice.setStatus(InvoiceStatus.UNPAID);
			invRepo.save(childInvoice);
		}
		Timeline tlParent = new Timeline();
		tlParent.setCreatedBy(createdBy);
		tlParent.setCreated(timeNow);
		tlParent.setInternal(true);
		tlParent.setMessage("Child invoice created : " + childInvoice.getInvoiceCode());
		tlParent.setObjectId(invoice.getId());
		tlParent.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tlParent);
		Timeline tlChild = new Timeline();
		tlChild.setCreatedBy(createdBy);
		tlChild.setCreated(timeNow);
		tlChild.setInternal(true);
		tlChild.setMessage("Invoice created");
		tlChild.setObjectId(childInvoice.getId());
		tlChild.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tlChild);
		Timeline tlChildNot = new Timeline();
		tlChildNot.setCreatedBy(createdBy);
		tlChildNot.setCreated(timeNow);
		tlChildNot.setInternal(true);
		tlChildNot.setMessage("Notification sent to consumer");
		tlChildNot.setObjectId(childInvoice.getId());
		tlChildNot.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tlChildNot);
		return childInvoice;
	}

	@Async
	@Transactional
	public void uploadConsumers(String invoiceCode, MultipartFile consumers, String createdBy) throws IOException {
		List<BulkUpload> bulkUploads = bulkUpdRepo.findByInvoiceCode(invoiceCode);
		Invoice parenInv = invRepo.findByInvoiceCode(invoiceCode);
		String fileName = invoiceCode + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkCsvLocation() + fileName;
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
		BulkUpload bun = new BulkUpload();
		bun.setCreated(timeNow);
		bun.setFileName(fileName);
		bun.setInvoiceCode(invoiceCode);
		bun.setCreatedBy(createdBy);
		bulkUpdRepo.save(bun);
		Timeline tlParent = new Timeline();
		tlParent.setCreatedBy(createdBy);
		tlParent.setCreated(timeNow);
		tlParent.setInternal(true);
		tlParent.setMessage("Consumers csv uploaded");
		tlParent.setObjectId(parenInv.getId());
		tlParent.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tlParent);
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
				chldInvReq.setConsumer(consumer);
				createChild(invoiceCode, chldInvReq, createdBy);
			}
			StringBuilder sb = new StringBuilder("");
			for (ConsumerCategory conCat : chldInvReq.getConCatList()) {
				sb.append(conCat.getName() + " : " + conCat.getValue() + ", ");
			}
			buc.setCategories(sb.toString());
			buc.setMessage("SUCCESS");
			Timeline tlParent = new Timeline();
			tlParent.setCreatedBy(createdBy);
			tlParent.setCreated(timeNow);
			tlParent.setInternal(true);
			tlParent.setMessage("Child invoices created for categories");
			tlParent.setObjectId(parenInv.getId());
			tlParent.setObjectType(ObjectType.INVOICE);
			tlRepo.save(tlParent);
		}
		bulkCatRepo.save(buc);
	}

}
