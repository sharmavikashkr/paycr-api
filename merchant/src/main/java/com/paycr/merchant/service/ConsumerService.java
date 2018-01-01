package com.paycr.merchant.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.Server;
import com.paycr.common.bean.UpdateConsumerRequest;
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkConsumerUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.BulkConsumerUploadRepository;
import com.paycr.common.data.repository.ConsumerCategoryRepository;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.AddressType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.merchant.validation.ConsumerValidator;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ConsumerService {

	@Autowired
	private ConsumerRepository conRepo;

	@Autowired
	private ConsumerCategoryRepository conCatRepo;

	@Autowired
	private BulkConsumerUploadRepository blkConUpldRepo;

	@Autowired
	private Server server;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ConsumerValidator conVal;

	@Autowired
	private ConsumerDao conDao;

	public void newConsumer(Consumer consumer, Merchant merchant, String createdBy) {
		List<ConsumerCategory> conCats = consumer.getConCats();
		consumer.setMerchant(merchant);
		consumer.setEmailOnPay(true);
		consumer.setEmailOnRefund(true);
		conVal.validate(consumer);
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy(createdBy);
		conRepo.save(consumer);
		for (ConsumerCategory conCat : conCats) {
			addCategory(consumer.getId(), conCat, merchant);
		}
	}

	public void updateConsumer(Consumer consumer, Integer consumerId) {
		Consumer exstCon = conRepo.findOne(consumerId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (exstCon.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		exstCon.setGstin(consumer.getGstin());
		exstCon.setActive(consumer.isActive());
		exstCon.setEmailOnPay(consumer.isEmailOnPay());
		exstCon.setEmailOnRefund(consumer.isEmailOnRefund());
		conRepo.save(exstCon);
	}

	public List<ConsumerCategory> getCategories(Integer consumerId) {
		Consumer consumer = conRepo.findOne(consumerId);
		if (consumer == null) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		return conCatRepo.findByConsumer(consumer);
	}

	public void addCategory(Integer consumerId, ConsumerCategory conCat, Merchant merchant) {
		Consumer consumer = conRepo.findOne(consumerId);
		if (consumer == null) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		if (conCat.getName() == null || conCat.getName().isEmpty() || conCat.getValue() == null
				|| conCat.getValue().isEmpty()) {
			throw new PaycrException(Constants.FAILURE, "Invalid category name/value");
		}
		ConsumerCategory exstConCat = conCatRepo.findByConsumerAndName(consumer, conCat.getName());
		if (exstConCat != null) {
			exstConCat.setValue(conCat.getValue());
			conCatRepo.save(exstConCat);
		} else {
			consumer = conRepo.findOne(consumerId);
			if (consumer.getConCats() != null && consumer.getConCats().size() >= 5) {
				throw new PaycrException(Constants.FAILURE, "Only 5 categories per consumer allowed");
			}
			conCat.setConsumer(consumer);
			conCatRepo.save(conCat);
		}
	}

	public void deleteCategory(Integer consumerId, Integer conCatId, Merchant merchant) {
		Consumer consumer = conRepo.findByMerchantAndId(merchant, consumerId);
		if (consumer == null) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		if (conCatRepo.findByConsumerAndId(consumer, conCatId) != null) {
			conCatRepo.delete(conCatId);
		}
	}

	public List<String> getCategories() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return conCatRepo.findCategoriesForMerchant(merchant);
	}

	public List<String> getCategoryValues(String category) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return conCatRepo.findValuesForCategory(merchant, category);
	}

	@Async
	@Transactional
	public void updateConsumerCategory(UpdateConsumerRequest updateReq, Merchant merchant) {
		Set<Consumer> consumerList = conDao.findConsumers(updateReq.getSearchReq(), merchant);
		for (Consumer consumer : consumerList) {
			consumer.setActive(updateReq.isActive());
			consumer.setEmailOnPay(updateReq.isEmailOnPay());
			consumer.setEmailOnRefund(updateReq.isEmailOnRefund());
			if (updateReq.isRemoveOldTags()) {
				conCatRepo.deleteForConsumer(consumer);
			}
			for (ConsumerCategory conCat : updateReq.getConCatList()) {
				ConsumerCategory newConCat = new ConsumerCategory();
				newConCat.setName(conCat.getName());
				newConCat.setValue(conCat.getValue());
				addCategory(consumer.getId(), newConCat, merchant);
			}
		}
	}

	@Async
	@Transactional
	public void uploadConsumers(MultipartFile consumers, Merchant merchant, String createdBy) throws IOException {
		List<BulkConsumerUpload> bulkUploads = blkConUpldRepo.findByMerchant(merchant);
		String fileName = merchant.getAccessKey() + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkConsumerLocation() + fileName;
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
			if (consumer.length == 3 || consumer.length == 4 || consumer.length == 6 || consumer.length == 12) {
				try {
					Consumer con = new Consumer();
					con.setName(consumer[0].trim());
					con.setEmail(consumer[1].trim());
					con.setMobile(consumer[2].trim());
					if (consumer.length > 3) {
						con.setGstin(consumer[3].trim());
					}
					if (consumer.length > 4) {
						ConsumerCategory conCat = new ConsumerCategory();
						conCat.setName(consumer[4].trim());
						conCat.setValue(consumer[5].trim());
						List<ConsumerCategory> conCats = new ArrayList<ConsumerCategory>();
						conCats.add(conCat);
						con.setConCats(conCats);
					}
					if (consumer.length > 6) {
						Address billAddr = new Address();
						billAddr.setAddressLine1(consumer[6].trim());
						billAddr.setAddressLine2(consumer[7].trim());
						billAddr.setCity(consumer[8].trim());
						billAddr.setState(consumer[9].trim());
						billAddr.setPincode(consumer[10].trim());
						billAddr.setCountry(consumer[11].trim());
						validateAddress(billAddr);
						con.setBillingAddress(billAddr);
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

	public byte[] downloadFile(String filename) throws IOException {
		Path path = Paths.get(server.getBulkConsumerLocation() + filename);
		return Files.readAllBytes(path);
	}

	public void updateConsumerAddress(Address addr, Integer consumerId) {
		validateAddress(addr);
		Consumer consumer = conRepo.findOne(consumerId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (consumer.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		Address address = null;
		if (AddressType.BILLING.equals(addr.getType())) {
			address = consumer.getBillingAddress();
			if (address == null) {
				address = new Address();
			}
			consumer.setBillingAddress(address);

		} else {
			address = consumer.getShippingAddress();
			if (address == null) {
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
			throw new PaycrException(Constants.FAILURE, "Invalid Address");
		}
	}
}
