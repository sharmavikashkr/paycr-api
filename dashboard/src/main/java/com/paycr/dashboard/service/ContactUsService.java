package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.ContactUs;
import com.paycr.common.data.repository.ContactUsRepository;
import com.paycr.common.util.CommonUtil;

@Service
public class ContactUsService {

	@Autowired
	private ContactUsRepository cntUsRepo;

	public void contactUs(ContactUs contactUs) {
		contactUs.setCreated(new Date());
		contactUs.setResolved(false);
		cntUsRepo.save(contactUs);
	}

	public List<ContactUs> getContactUs(String email, String type, int page, boolean resolved) {
		Pageable pagReq = new PageRequest(page, 20);
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
		ContactUs cntUs = cntUsRepo.findOne(id);
		if (CommonUtil.isNotNull(cntUs)) {
			cntUs.setResolved(!cntUs.isResolved());
			cntUsRepo.save(cntUs);
		}
	}

}
