package com.paycr.dashboard.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class InventoryService {

	@Autowired
	private InventoryRepository invnRepo;

	@Autowired
	private SecurityService secSer;

	public List<Inventory> getMyInventory() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return invnRepo.findByMerchant(merchant);
	}

	public void newInventory(Inventory inventory) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isEmpty(inventory.getName()) || CommonUtil.isNull(inventory.getRate())
				|| CommonUtil.isEmpty(inventory.getCode())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		Inventory exstingInvn = invnRepo.findByMerchantAndCode(merchant, inventory.getCode());
		if (CommonUtil.isNull(exstingInvn)) {
			throw new PaycrException(Constants.FAILURE, "Item with this code already exists");
		}
		inventory.setCreated(new Date());
		inventory.setMerchant(merchant);
		inventory.setCreatedBy(user.getEmail());
		invnRepo.save(inventory);
	}
}
