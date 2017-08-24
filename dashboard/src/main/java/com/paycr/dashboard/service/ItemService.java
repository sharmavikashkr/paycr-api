package com.paycr.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.service.SecurityService;

@Service
public class ItemService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private SecurityService secSer;

	public List<Item> getAllItems() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return invRepo.findItemsForMerchant(merchant);
	}
}
