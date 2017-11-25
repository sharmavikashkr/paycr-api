package com.paycr.dashboard.service;

import java.util.Date;

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

	public void newInventory(Inventory inventory) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isEmpty(inventory.getName()) || CommonUtil.isNull(inventory.getRate())
				|| CommonUtil.isEmpty(inventory.getCode())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		Inventory exstingInvn = invnRepo.findByMerchantAndCode(merchant, inventory.getCode());
		if (!CommonUtil.isNull(exstingInvn)) {
			throw new PaycrException(Constants.FAILURE, "Item with this code already exists");
		}
		inventory.setCreated(new Date());
		inventory.setMerchant(merchant);
		inventory.setCreatedBy(user.getEmail());
		inventory.setActive(true);
		invnRepo.save(inventory);
	}

	public void updateInventory(Inventory inventory, Integer inventoryId) {
		Inventory exstInvn = invnRepo.findOne(inventoryId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (exstInvn.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(Constants.FAILURE, "Inventory not found");
		}
		exstInvn.setActive(inventory.isActive());
		exstInvn.setDescription(inventory.getDescription());
		invnRepo.save(exstInvn);
	}
}
