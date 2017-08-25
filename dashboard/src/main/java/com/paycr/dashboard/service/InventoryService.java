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
		if (CommonUtil.isEmpty(inventory.getName()) || CommonUtil.isNull(inventory.getRate())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		Inventory exstingInvn = invnRepo.findByMerchantAndNameAndRate(merchant, inventory.getName(),
				inventory.getRate());
		if (exstingInvn != null) {
			throw new PaycrException(Constants.FAILURE, "Item Already Exists");
		}
		inventory.setCreated(new Date());
		inventory.setMerchant(merchant);
		inventory.setCreatedBy(user.getEmail());
		invnRepo.save(inventory);
	}
}
