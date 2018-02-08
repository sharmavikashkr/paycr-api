package com.paycr.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.common.bean.search.SearchMerchantRequest;
import com.paycr.common.bean.search.SearchPromotionRequest;
import com.paycr.common.bean.search.SearchSubsRequest;
import com.paycr.common.data.dao.MerchantDao;
import com.paycr.common.data.dao.PromotionDao;
import com.paycr.common.data.dao.SubscriptionDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.PaycrUtil;

@Service
public class AdminSearchService {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminSearchService.class);

	@Autowired
	private MerchantDao merDao;

	@Autowired
	private SubscriptionDao subsDao;

	@Autowired
	private PromotionDao promoDao;

	public List<Merchant> fetchMerchantList(SearchMerchantRequest request) {
		logger.info("Search merchant started for request : {}",new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return merDao.findMerchants(request);
	}

	public List<Subscription> fetchSubsList(SearchSubsRequest request) {
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return subsDao.findSubscriptions(request);
	}

	public List<Promotion> fetchPromotionList(SearchPromotionRequest request) {
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return promoDao.findPromotions(request);
	}

}
