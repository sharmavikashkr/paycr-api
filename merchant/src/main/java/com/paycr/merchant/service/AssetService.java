package com.paycr.merchant.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.paycr.common.awss3.AwsS3Folder;
import com.paycr.common.awss3.AwsS3Service;
import com.paycr.common.bean.AssetStats;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.BulkAssetUpload;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.AssetRepository;
import com.paycr.common.data.repository.BulkAssetUploadRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.ItemType;
import com.paycr.common.util.CommonUtil;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class AssetService {

	private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

	private int maxUploadSizeInMb = 5 * 1024 * 1024;

	@Autowired
	private AssetRepository assetRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private BulkAssetUploadRepository blkAssetUpldRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private Server server;

	@Autowired
	private AwsS3Service awsS3Ser;

	public void newAsset(Asset asset, Merchant merchant, String createdBy) {
		logger.info("New Asset request : {}", new Gson().toJson(asset));
		if (CommonUtil.isEmpty(asset.getName()) || CommonUtil.isNull(asset.getRate())
				|| CommonUtil.isEmpty(asset.getCode()) || CommonUtil.isNull(asset.getType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
		Asset exstingInvn = assetRepo.findByMerchantAndCode(merchant, asset.getCode());
		if (!CommonUtil.isNull(exstingInvn)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Item with this code already exists");
		}
		if (CommonUtil.isNull(asset.getTax())) {
			asset.setTax(taxMRepo.findByName("NO_TAX"));
		}
		asset.setCreated(new Date());
		asset.setMerchant(merchant);
		asset.setCreatedBy(createdBy);
		asset.setType(ItemType.SERVICE);
		asset.setActive(true);
		assetRepo.save(asset);
	}

	public void updateAsset(Asset asset, Integer assetId) {
		logger.info("Update Asset request : {}", new Gson().toJson(asset));
		Asset exstInvn = assetRepo.findOne(assetId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (exstInvn.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Asset not found");
		}
		exstInvn.setActive(asset.isActive());
		exstInvn.setDescription(asset.getDescription());
		exstInvn.setTax(asset.getTax());
		exstInvn.setHsnsac(asset.getHsnsac());
		exstInvn.setType(asset.getType());
		assetRepo.save(exstInvn);
	}

	public AssetStats getStats(Integer assetId) {
		logger.info("Get Asset stats : {}", assetId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		AssetStats response = new AssetStats();
		List<Object[]> paidCounts = assetRepo.findCountAndSumForMerchant(merchant, assetId, ExpenseStatus.PAID);
		List<Object[]> unpaidCounts = assetRepo.findCountAndSumForMerchant(merchant, assetId, ExpenseStatus.UNPAID);
		response.setPaidNo((Long) paidCounts.get(0)[0] == null ? 0L : (Long) paidCounts.get(0)[0]);
		response.setPaidSum(
				(BigDecimal) paidCounts.get(0)[1] == null ? BigDecimal.valueOf(0D) : (BigDecimal) paidCounts.get(0)[1]);
		response.setUnpaidNo((Long) unpaidCounts.get(0)[0] == null ? 0L : (Long) unpaidCounts.get(0)[0]);
		response.setUnpaidSum((BigDecimal) unpaidCounts.get(0)[1] == null ? BigDecimal.valueOf(0D)
				: (BigDecimal) unpaidCounts.get(0)[1]);
		return response;
	}

	public List<BulkAssetUpload> getUploads(Merchant merchant) {
		logger.info("Get Asset uploads by : {}", merchant.getAccessKey());
		return blkAssetUpldRepo.findByMerchant(merchant);
	}

	public byte[] downloadFile(String accessKey, String fileName) throws IOException {
		logger.info("Download bulk asset file : {}", fileName);
		return awsS3Ser.getFile(accessKey.concat("/").concat(AwsS3Folder.ASSET), fileName);
	}

	@Async
	@Transactional
	public void uploadAsset(MultipartFile assetFile, Merchant merchant, String createdBy) throws IOException {
		logger.info("Upload bulk asset : {}", assetFile.getName());
		if (maxUploadSizeInMb < assetFile.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 5MBs");
		}
		List<BulkAssetUpload> bulkUploads = blkAssetUpldRepo.findByMerchant(merchant);
		String fileName = merchant.getAccessKey() + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkAssetLocation() + fileName;
		CSVWriter writer = new CSVWriter(new FileWriter(updatedCsv, true));
		Reader reader = new InputStreamReader(assetFile.getInputStream());
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> assetList = csvReader.readAll();
		csvReader.close();
		if (CommonUtil.isEmpty(assetList) || assetList.size() > 200) {
			String[] record = new String[1];
			record[0] = "Min 1 and Max 200 asset can be uploaded";
			writer.writeNext(record);
		}
		for (String[] asset : assetList) {
			String[] record = new String[asset.length + 1];
			for (int i = 0; i < asset.length; i++) {
				record[i] = asset[i];
			}
			String reason = "Invalid format";
			if (asset.length == 5) {
				try {
					Asset ast = new Asset();
					ast.setCode(asset[0].trim());
					ast.setName(asset[1].trim());
					ast.setRate(new BigDecimal(asset[2].trim()));
					ast.setHsnsac(asset[3].trim());
					ast.setDescription(asset[4].trim());
					newAsset(ast, merchant, createdBy);
					reason = "CREATED";
				} catch (PaycrException ex) {
					reason = ex.getMessage();
				} catch (Exception ex) {
					reason = "Something went wrong";
				}
			}
			record[asset.length] = reason;
			writer.writeNext(record);
		}
		writer.close();
		awsS3Ser.saveFile(merchant.getAccessKey().concat("/").concat(AwsS3Folder.INVENTORY), new File(updatedCsv));
		Date timeNow = new Date();
		BulkAssetUpload bcu = new BulkAssetUpload();
		bcu.setCreated(timeNow);
		bcu.setFileName(fileName);
		bcu.setMerchant(merchant);
		bcu.setCreatedBy(createdBy);
		blkAssetUpldRepo.save(bcu);
	}
}
