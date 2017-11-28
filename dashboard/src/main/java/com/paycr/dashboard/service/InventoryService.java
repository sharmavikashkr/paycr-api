package com.paycr.dashboard.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.InventoryStats;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
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

	public InventoryStats getStats(Integer inventoryId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		InventoryStats response = new InventoryStats();
		List<Object[]> paidCounts = invnRepo.findCountAndSumForMerchant(merchant, inventoryId, InvoiceStatus.PAID);
		List<Object[]> unpaidCounts = invnRepo.findCountAndSumForMerchant(merchant, inventoryId, InvoiceStatus.UNPAID);
		List<Object[]> createdCounts = invnRepo.findCountAndSumForMerchant(merchant, inventoryId,
				InvoiceStatus.CREATED);
		List<Object[]> expiredCounts = invnRepo.findCountAndSumForMerchant(merchant, inventoryId,
				InvoiceStatus.EXPIRED);
		List<Object[]> declinedCounts = invnRepo.findCountAndSumForMerchant(merchant, inventoryId,
				InvoiceStatus.DECLINED);
		response.setPaidNo((Long) paidCounts.get(0)[0] == null ? 0L : (Long) paidCounts.get(0)[0]);
		response.setPaidSum(
				(BigDecimal) paidCounts.get(0)[1] == null ? BigDecimal.valueOf(0D) : (BigDecimal) paidCounts.get(0)[1]);
		response.setUnpaidNo((Long) unpaidCounts.get(0)[0] == null ? 0L : (Long) unpaidCounts.get(0)[0]);
		response.setUnpaidSum((BigDecimal) unpaidCounts.get(0)[1] == null ? BigDecimal.valueOf(0D)
				: (BigDecimal) unpaidCounts.get(0)[1]);
		response.setExpiredNo((Long) expiredCounts.get(0)[0] == null ? 0L : (Long) expiredCounts.get(0)[0]);
		response.setExpiredSum((BigDecimal) expiredCounts.get(0)[1] == null ? BigDecimal.valueOf(0D)
				: (BigDecimal) expiredCounts.get(0)[1]);
		response.setCreatedNo((Long) createdCounts.get(0)[0] == null ? 0L : (Long) createdCounts.get(0)[0]);
		response.setCreatedSum((BigDecimal) createdCounts.get(0)[1] == null ? BigDecimal.valueOf(0D)
				: (BigDecimal) createdCounts.get(0)[1]);
		response.setDeclinedNo((Long) declinedCounts.get(0)[0] == null ? 0L : (Long) declinedCounts.get(0)[0]);
		response.setDeclinedSum((BigDecimal) declinedCounts.get(0)[1] == null ? BigDecimal.valueOf(0D)
				: (BigDecimal) declinedCounts.get(0)[1]);
		return response;
	}
}
