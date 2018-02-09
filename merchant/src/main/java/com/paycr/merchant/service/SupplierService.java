package com.paycr.merchant.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkSupplierUpload;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.BulkSupplierUploadRepository;
import com.paycr.common.data.repository.SupplierRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.StateHelper;
import com.paycr.dashboard.validation.IsValidGstinRequest;
import com.paycr.merchant.validation.SupplierValidator;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Service
public class SupplierService {

	@Autowired
	private SupplierRepository conRepo;

	@Autowired
	private BulkSupplierUploadRepository blkSupUpldRepo;

	@Autowired
	private Server server;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private IsValidGstinRequest gstinValid;

	@Autowired
	private SupplierValidator conVal;

	public void newSupplier(Supplier supplier, Merchant merchant, String createdBy) {
		supplier.setMerchant(merchant);
		conVal.validate(supplier);
		gstinValid.validate(supplier.getGstin());
		supplier.setActive(true);
		supplier.setCreated(new Date());
		supplier.setCreatedBy(createdBy);
		conRepo.save(supplier);
	}

	public void updateSupplier(Supplier supplier, Integer supplierId) {
		Supplier exstCon = conRepo.findOne(supplierId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (exstCon.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Supplier not found");
		}
		exstCon.setGstin(supplier.getGstin());
		exstCon.setActive(supplier.isActive());
		gstinValid.validate(supplier.getGstin());
		conRepo.save(exstCon);
	}

	@Async
	@Transactional
	public void uploadSuppliers(MultipartFile suppliers, Merchant merchant, String createdBy) throws IOException {
		List<BulkSupplierUpload> bulkUploads = blkSupUpldRepo.findByMerchant(merchant);
		String fileName = merchant.getAccessKey() + "-" + bulkUploads.size() + ".csv";
		String updatedCsv = server.getBulkSupplierLocation() + fileName;
		CSVWriter writer = new CSVWriter(new FileWriter(updatedCsv, true));
		Reader reader = new InputStreamReader(suppliers.getInputStream());
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> supplierList = csvReader.readAll();
		csvReader.close();
		if (CommonUtil.isEmpty(supplierList) || supplierList.size() > 200) {
			String[] record = new String[1];
			record[0] = "Min 1 and Max 200 suppliers can be uploaded";
			writer.writeNext(record);
		}
		for (String[] supplier : supplierList) {
			String[] record = new String[supplier.length + 1];
			for (int i = 0; i < supplier.length; i++) {
				record[i] = supplier[i];
			}
			String reason = "Invalid format";
			if (supplier.length == 3 || supplier.length == 4 || supplier.length == 10) {
				try {
					Supplier con = new Supplier();
					con.setName(supplier[0].trim());
					con.setEmail(supplier[1].trim());
					con.setMobile(supplier[2].trim());
					if (supplier.length > 3) {
						con.setGstin(supplier[3].trim());
					}
					if (supplier.length > 4) {
						Address billAddr = new Address();
						billAddr.setAddressLine1(supplier[4].trim());
						billAddr.setAddressLine2(supplier[5].trim());
						billAddr.setCity(supplier[6].trim());
						billAddr.setState(StateHelper.getStateForCode(supplier[7].trim()));
						billAddr.setPincode(supplier[8].trim());
						billAddr.setCountry(supplier[9].trim());
						validateAddress(billAddr);
						con.setAddress(billAddr);
					}
					newSupplier(con, merchant, createdBy);
					reason = "CREATED";
				} catch (PaycrException ex) {
					reason = ex.getMessage();
				} catch (Exception ex) {
					reason = "Something went wrong";
				}
			}
			record[supplier.length] = reason;
			writer.writeNext(record);
		}
		writer.close();
		Date timeNow = new Date();
		BulkSupplierUpload bcu = new BulkSupplierUpload();
		bcu.setCreated(timeNow);
		bcu.setFileName(fileName);
		bcu.setMerchant(merchant);
		bcu.setCreatedBy(createdBy);
		blkSupUpldRepo.save(bcu);
	}

	public List<BulkSupplierUpload> getUploads(Merchant merchant) {
		return blkSupUpldRepo.findByMerchant(merchant);
	}

	public byte[] downloadFile(String filename) throws IOException {
		Path path = Paths.get(server.getBulkSupplierLocation() + filename);
		return Files.readAllBytes(path);
	}

	public void updateSupplierAddress(Address addr, Integer supplierId) {
		validateAddress(addr);
		Supplier supplier = conRepo.findOne(supplierId);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (supplier.getMerchant().getId() != merchant.getId()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Supplier not found");
		}
		Address address = supplier.getAddress();
		if (CommonUtil.isNull(address)) {
			address = new Address();
		}
		supplier.setAddress(address);
		address.setAddressLine1(addr.getAddressLine1());
		address.setAddressLine2(addr.getAddressLine2());
		address.setCity(addr.getCity());
		address.setState(addr.getState());
		address.setCountry(addr.getCountry());
		address.setPincode(addr.getPincode());
		conRepo.save(supplier);
	}

	private void validateAddress(Address addr) {
		if (CommonUtil.isNull(addr) || CommonUtil.isEmpty(addr.getAddressLine1()) || CommonUtil.isEmpty(addr.getCity())
				|| CommonUtil.isEmpty(addr.getState()) || CommonUtil.isEmpty(addr.getPincode())
				|| CommonUtil.isEmpty(addr.getCountry())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Address");
		}
	}
}
