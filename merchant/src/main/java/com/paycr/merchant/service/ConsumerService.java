package com.paycr.merchant.service;

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
import com.paycr.common.bean.Server;
import com.paycr.common.bean.UpdateConsumerRequest;
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkConsumerUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.BulkConsumerUploadRepository;
import com.paycr.common.data.repository.ConsumerFlagRepository;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.AddressType;
import com.paycr.common.type.ConsumerType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.StateHelper;
import com.paycr.dashboard.validation.IsValidGstinRequest;
import com.paycr.merchant.validation.ConsumerValidator;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ConsumerService {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

	private int maxUploadSizeInMb = 5 * 1024 * 1024;

	@Autowired
	private ConsumerRepository conRepo;

	@Autowired
	private ConsumerFlagRepository flagRepo;

	@Autowired
	private BulkConsumerUploadRepository blkConUpldRepo;

	@Autowired
	private Server server;

	@Autowired
	private AwsS3Service awsS3Ser;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ConsumerValidator conVal;

	@Autowired
	private IsValidGstinRequest gstinValid;

	@Autowired
	private ConsumerDao conDao;

	public void newConsumer(Consumer consumer, Merchant merchant, String createdBy) {
		logger.info("New Consumer request : {}", new Gson().toJson(consumer));
		List<ConsumerFlag> flags = consumer.getFlags();
		consumer.setMerchant(merchant);
		consumer.setEmailOnPay(true);
		consumer.setEmailOnRefund(true);
		conVal.validate(consumer);
		gstinValid.validate(consumer.getGstin());
		if (CommonUtil.isEmpty(consumer.getGstin())) {
			consumer.setType(ConsumerType.CUSTOMER);
		} else {
			consumer.setType(ConsumerType.BUSINESS);
		}
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy(createdBy);
		conRepo.save(consumer);
		for (ConsumerFlag flag : flags) {
			addFlag(consumer.getId(), flag, merchant);
		}
	}

	public void updateConsumer(Consumer consumer, Integer consumerId) {
		logger.info("Update Consumer request : {}", new Gson().toJson(consumer));
		Consumer exstCon = conRepo.findOne(consumerId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (exstCon.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not found");
		}
		if (ConsumerType.CUSTOMER.equals(consumer.getType()) && !CommonUtil.isEmpty(consumer.getGstin())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "GSTIN holder needs to be of type Business");
		} else if (ConsumerType.BUSINESS.equals(consumer.getType()) && CommonUtil.isEmpty(consumer.getGstin())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Please update GSTIN for Business");
		}
		exstCon.setGstin(consumer.getGstin());
		exstCon.setActive(consumer.isActive());
		exstCon.setEmailOnPay(consumer.isEmailOnPay());
		exstCon.setEmailOnRefund(consumer.isEmailOnRefund());
		exstCon.setType(consumer.getType());
		gstinValid.validate(consumer.getGstin());
		conRepo.save(exstCon);
	}

	public void addFlag(Integer consumerId, ConsumerFlag flag, Merchant merchant) {
		logger.info("Add Consumer flag : {} for consumer : {}", new Gson().toJson(flag), consumerId);
		Consumer consumer = conRepo.findOne(consumerId);
		if (CommonUtil.isNull(consumer)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not found");
		}
		if (CommonUtil.isEmpty(flag.getName())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid flag name");
		}
		ConsumerFlag exstFlag = flagRepo.findByConsumerAndName(consumer, flag.getName());
		if (!CommonUtil.isNotNull(exstFlag)) {
			consumer = conRepo.findOne(consumerId);
			if (CommonUtil.isNotEmpty(consumer.getFlags()) && consumer.getFlags().size() >= 5) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Only 5 flags per consumer allowed");
			}
			flag.setConsumer(consumer);
			flagRepo.save(flag);
		}
	}

	public void deleteFlag(Integer consumerId, Integer conCatId, Merchant merchant) {
		logger.info("Delete Consumer flag : {} for consumer : {}", conCatId, consumerId);
		Consumer consumer = conRepo.findByMerchantAndId(merchant, consumerId);
		if (CommonUtil.isNull(consumer)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not found");
		}
		if (CommonUtil.isNotNull(flagRepo.findByConsumerAndId(consumer, conCatId))) {
			flagRepo.delete(conCatId);
		}
	}

	public List<String> getFlags() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return flagRepo.findFlagsForMerchant(merchant);
	}

	@Async
	@Transactional
	public void updateConsumerFlag(UpdateConsumerRequest updateReq, Merchant merchant) {
		logger.info("Bulk Update Consumer : {}", new Gson().toJson(updateReq));
		Set<Consumer> consumerList = conDao.findConsumers(updateReq.getSearchReq(), merchant);
		for (Consumer consumer : consumerList) {
			consumer.setActive(updateReq.isActive());
			consumer.setEmailOnPay(updateReq.isEmailOnPay());
			consumer.setEmailOnRefund(updateReq.isEmailOnRefund());
			if (updateReq.isRemoveOldTags()) {
				flagRepo.deleteForConsumer(consumer);
			}
			for (ConsumerFlag flag : updateReq.getFlagList()) {
				ConsumerFlag newFlag = new ConsumerFlag();
				newFlag.setName(flag.getName());
				addFlag(consumer.getId(), newFlag, merchant);
			}
		}
	}

	@Async
	@Transactional
	public void uploadConsumers(MultipartFile consumerFile, Merchant merchant, String createdBy) throws IOException {
		logger.info("Bulk Upload Consumer : {}", consumerFile.getName());
		if (maxUploadSizeInMb < consumerFile.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 5MBs");
		}
		List<BulkConsumerUpload> bulkUploads = blkConUpldRepo.findByMerchant(merchant);
		String fileName = merchant.getAccessKey() + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkConsumerLocation() + fileName;
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
			if (consumer.length == 3 || consumer.length == 4 || consumer.length == 5 || consumer.length == 11
					|| consumer.length == 12) {
				try {
					Consumer con = new Consumer();
					con.setName(consumer[0].trim());
					con.setEmail(consumer[1].trim());
					con.setMobile(consumer[2].trim());
					if (consumer.length > 3 && !CommonUtil.isEmpty(consumer[3].trim())) {
						con.setGstin(consumer[3].trim());
					}
					if (consumer.length > 4) {
						ConsumerFlag conCat = new ConsumerFlag();
						conCat.setName(consumer[4].trim());
						List<ConsumerFlag> flags = new ArrayList<ConsumerFlag>();
						flags.add(conCat);
						con.setFlags(flags);
					}
					if (consumer.length > 5) {
						Address billAddr = new Address();
						billAddr.setAddressLine1(consumer[5].trim());
						billAddr.setAddressLine2(consumer[6].trim());
						billAddr.setCity(consumer[7].trim());
						billAddr.setState(StateHelper.getStateForCode(consumer[8].trim()));
						billAddr.setPincode(consumer[9].trim());
						billAddr.setCountry(consumer[10].trim());
						validateAddress(billAddr);
						con.setBillingAddress(billAddr);
					}
					if (consumer.length > 11) {
						if ("YES".equalsIgnoreCase(consumer[11].trim())) {
							Address shipAddr = new Address();
							shipAddr.setAddressLine1(consumer[5].trim());
							shipAddr.setAddressLine2(consumer[6].trim());
							shipAddr.setCity(consumer[7].trim());
							shipAddr.setState(consumer[8].trim());
							shipAddr.setPincode(consumer[9].trim());
							shipAddr.setCountry(consumer[10].trim());
							validateAddress(shipAddr);
							con.setShippingAddress(shipAddr);
						}
					}
					newConsumer(con, merchant, createdBy);
					reason = "CREATED";
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
		awsS3Ser.saveFile(merchant.getAccessKey().concat("/").concat(AwsS3Folder.CONSUMER), new File(updatedCsv));
		Date timeNow = new Date();
		BulkConsumerUpload bcu = new BulkConsumerUpload();
		bcu.setCreated(timeNow);
		bcu.setFileName(fileName);
		bcu.setMerchant(merchant);
		bcu.setCreatedBy(createdBy);
		blkConUpldRepo.save(bcu);
	}

	public List<BulkConsumerUpload> getUploads(Merchant merchant) {
		return blkConUpldRepo.findByMerchant(merchant);
	}

	public byte[] downloadFile(String accessKey, String fileName) throws IOException {
		return awsS3Ser.getFile(accessKey.concat("/").concat(AwsS3Folder.CONSUMER), fileName);
	}

	public void updateConsumerAddress(Address addr, Integer consumerId) {
		validateAddress(addr);
		Consumer consumer = conRepo.findOne(consumerId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (consumer.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not found");
		}
		Address address = null;
		if (AddressType.BILLING.equals(addr.getType())) {
			address = consumer.getBillingAddress();
			if (CommonUtil.isNull(address)) {
				address = new Address();
			}
			consumer.setBillingAddress(address);

		} else {
			address = consumer.getShippingAddress();
			if (CommonUtil.isNull(address)) {
				address = new Address();
			}
			consumer.setShippingAddress(address);
		}
		address.setAddressLine1(addr.getAddressLine1());
		address.setAddressLine2(addr.getAddressLine2());
		address.setCity(addr.getCity());
		address.setState(addr.getState());
		address.setCountry(addr.getCountry());
		address.setPincode(addr.getPincode());
		conRepo.save(consumer);
	}

	private void validateAddress(Address addr) {
		if (CommonUtil.isNull(addr) || CommonUtil.isEmpty(addr.getAddressLine1()) || CommonUtil.isEmpty(addr.getCity())
				|| CommonUtil.isEmpty(addr.getState()) || CommonUtil.isEmpty(addr.getPincode())
				|| CommonUtil.isEmpty(addr.getCountry())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Address");
		}
	}
}
