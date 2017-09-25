package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.dashboard.validation.ConsumerValidator;

@Service
public class ConsumerService {

	@Autowired
	private ConsumerRepository conRepo;

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
		consumer.setOrganization("");
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
		exstCon.setOrganization(consumer.getOrganization());
		exstCon.setEmailOnPay(consumer.isEmailOnPay());
		exstCon.setEmailOnRefund(consumer.isEmailOnRefund());
		conRepo.save(exstCon);
	}
}
