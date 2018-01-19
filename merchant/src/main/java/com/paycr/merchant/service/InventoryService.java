package com.paycr.merchant.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.InventoryStats;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.BulkInventoryUpload;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.BulkInventoryUploadRepository;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class InventoryService {

	@Autowired
	private InventoryRepository invnRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private BulkInventoryUploadRepository blkInvnUpldRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private Server server;

	public void newInventory(Inventory inventory, Merchant merchant, String createdBy) {
		if (CommonUtil.isEmpty(inventory.getName()) || CommonUtil.isNull(inventory.getRate())
				|| CommonUtil.isEmpty(inventory.getCode()) || CommonUtil.isNull(inventory.getType())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		Inventory exstingInvn = invnRepo.findByMerchantAndCode(merchant, inventory.getCode());
		if (!CommonUtil.isNull(exstingInvn)) {
			throw new PaycrException(Constants.FAILURE, "Item with this code already exists");
		}
		if (CommonUtil.isNull(inventory.getTax())) {
			inventory.setTax(taxMRepo.findByName("NO_TAX"));
		}
		inventory.setCreated(new Date());
		inventory.setMerchant(merchant);
		inventory.setCreatedBy(createdBy);
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
		exstInvn.setTax(inventory.getTax());
		exstInvn.setHsnsac(inventory.getHsnsac());
		exstInvn.setType(inventory.getType());
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

	public List<BulkInventoryUpload> getUploads(Merchant merchant) {
		return blkInvnUpldRepo.findByMerchant(merchant);
	}

	public byte[] downloadFile(String filename) throws IOException {
		Path path = Paths.get(server.getBulkInventoryLocation() + filename);
		return Files.readAllBytes(path);
	}

	@Async
	@Transactional
	public void uploadInventory(MultipartFile inventoryFile, Merchant merchant, String createdBy) throws IOException {
		List<BulkInventoryUpload> bulkUploads = blkInvnUpldRepo.findByMerchant(merchant);
		String fileName = merchant.getAccessKey() + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkInventoryLocation() + fileName;
		CSVWriter writer = new CSVWriter(new FileWriter(updatedCsv, true));
		Reader reader = new InputStreamReader(inventoryFile.getInputStream());
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> inventoryList = csvReader.readAll();
		csvReader.close();
		if (inventoryList == null || inventoryList.isEmpty() || inventoryList.size() > 200) {
			String[] record = new String[1];
			record[0] = "Min 1 and Max 200 inventory can be uploaded";
			writer.writeNext(record);
		}
		for (String[] inventory : inventoryList) {
			String[] record = new String[inventory.length + 1];
			for (int i = 0; i < inventory.length; i++) {
				record[i] = inventory[i];
			}
			String reason = "Invalid format";
			if (inventory.length == 5) {
				try {
					Inventory invn = new Inventory();
					invn.setCode(inventory[0].trim());
					invn.setName(inventory[1].trim());
					invn.setRate(new BigDecimal(inventory[2].trim()));
					invn.setHsnsac(inventory[3].trim());
					invn.setDescription(inventory[4].trim());
					newInventory(invn, merchant, createdBy);
					reason = "CREATED";
				} catch (PaycrException ex) {
					reason = ex.getMessage();
				} catch (Exception ex) {
					reason = "Something went wrong";
				}
			}
			record[inventory.length] = reason;
			writer.writeNext(record);
		}
		writer.close();
		Date timeNow = new Date();
		BulkInventoryUpload bcu = new BulkInventoryUpload();
		bcu.setCreated(timeNow);
		bcu.setFileName(fileName);
		bcu.setMerchant(merchant);
		bcu.setCreatedBy(createdBy);
		blkInvnUpldRepo.save(bcu);
	}
}
