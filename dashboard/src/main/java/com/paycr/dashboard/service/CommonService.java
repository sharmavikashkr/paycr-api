package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.domain.Timeline;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.data.repository.TimelineRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.ObjectType;

@Service
public class CommonService {

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private TimelineRepository tlRepo;

	public List<Invoice> getMyInvoices(PcUser user) {
		return invRepo.findInvoicesForConsumer(user.getEmail(), user.getMobile());
	}

	public List<Pricing> getPricings() {
		return priceRepo.findAll();
	}

	public List<Notification> getNotifications() {
		Pageable topFour = new PageRequest(0, 4);
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(null, merchant.getId(), topFour);
		} else {
			PcUser user = secSer.findLoggedInUser();
			return notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(user.getId(), null, topFour);
		}
	}

	public List<Timeline> getTimelines(ObjectType objectType, Integer objectId) {
		return tlRepo.findByObjectTypeAndObjectId(objectType, objectId);
	}

	public void saveComment(Timeline tl) {
		PcUser user = secSer.findLoggedInUser();
		tl.setCreatedBy(user.getEmail());
		tl.setCreated(new Date());
		tl.setInternal(true);
		tlRepo.save(tl);
	}

	public List<TaxMaster> getTaxes() {
		return taxMRepo.findByActive(true);
	}

}
