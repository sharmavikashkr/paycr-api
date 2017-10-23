package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.ConsumerCategoryRepository;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.validation.ConsumerValidator;

@Service
public class ConsumerService {

	@Autowired
	private ConsumerRepository conRepo;

	@Autowired
	private ConsumerCategoryRepository conCatRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ConsumerValidator conVal;

	public List<Consumer> getAllConsumer() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return conRepo.findByMerchant(merchant);
	}

	public void newConsumer(Consumer consumer) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		consumer.setMerchant(merchant);
		consumer.setEmailOnPay(true);
		consumer.setEmailOnRefund(true);
		conVal.validate(consumer);
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy(user.getEmail());
		conRepo.save(consumer);
	}

	public void updateConsumer(Consumer consumer, Integer consumerId) {
		Consumer exstCon = conRepo.findOne(consumerId);
		exstCon.setActive(consumer.isActive());
		exstCon.setAddress(consumer.getAddress());
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

	public void addCategory(Integer consumerId, ConsumerCategory conCat) {
		Consumer consumer = conRepo.findOne(consumerId);
		if (consumer == null) {
			throw new PaycrException(Constants.FAILURE, "Consumer not found");
		}
		if (conCat.getName() == null || conCat.getName().isEmpty() || conCat.getValue() == null
				|| conCat.getValue().isEmpty()) {
			throw new PaycrException(Constants.FAILURE, "Invalid category name/value");
		}
		if (conCatRepo.findByConsumerAndName(consumer, conCat.getName()) != null) {
			throw new PaycrException(Constants.FAILURE, "Category already added for consumer");
		}
		conCat.setConsumer(consumer);
		conCatRepo.save(conCat);
	}

	public void deleteCategory(Integer consumerId, Integer conCatId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
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
}
