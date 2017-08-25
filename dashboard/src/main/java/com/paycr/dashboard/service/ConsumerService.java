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
		conVal.validate(consumer);
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy(user.getEmail());
		conRepo.save(consumer);
	}
}
