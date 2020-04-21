package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.common.data.domain.ContactUs;
import com.paycr.common.data.repository.ContactUsRepository;
import com.paycr.common.util.CommonUtil;

@Service
public class ContactUsService {

	private static final Logger logger = LoggerFactory.getLogger(ContactUsService.class);

	@Autowired
	private ContactUsRepository cntUsRepo;

	public void contactUs(ContactUs contactUs) {
		logger.info("New contact us : ", new Gson().toJson(contactUs));
		contactUs.setCreated(new Date());
		contactUs.setResolved(false);
		cntUsRepo.save(contactUs);
	}

	public List<ContactUs> getContactUs(String email, String type, int page, boolean resolved) {
		Pageable pagReq = PageRequest.of(page, 20);
		if (CommonUtil.isEmpty(email) && CommonUtil.isEmpty(type)) {
			return cntUsRepo.findLatest(resolved, pagReq);
		} else if (CommonUtil.isEmpty(email)) {
			return cntUsRepo.findByType(resolved, type, pagReq);
		} else if (CommonUtil.isEmpty(type)) {
			return cntUsRepo.findByEmail(resolved, email, pagReq);
		} else {
			return cntUsRepo.findByEmailAndType(resolved, email, type, pagReq);
		}
	}

	public void toggle(Integer id) {
		logger.info("Toggle contactUs : {}", id);
		Optional<ContactUs> cntUsOpt = cntUsRepo.findById(id);
		if (cntUsOpt.isPresent()) {
			ContactUs cntUs = cntUsOpt.get();
			cntUs.setResolved(!cntUs.isResolved());
			cntUsRepo.save(cntUs);
		}
	}

}
