package com.paycr.merchant.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkSupplierUpload;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.SupplierService;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

	@Autowired
	private SupplierService supSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/new")
	public void newSupplier(@Valid @RequestBody Supplier supplier) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		supSer.newSupplier(supplier, merchant, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{supplierId}")
	public void updateSupplier(@RequestBody Supplier supplier, @PathVariable Integer supplierId) {
		supSer.updateSupplier(supplier, supplierId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/address/update/{supplierId}")
	public void updateSupplierAddress(@RequestBody Address address, @PathVariable Integer supplierId) {
		supSer.updateSupplierAddress(address, supplierId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload", method = RequestMethod.POST)
	public void uploadSuppliers(@RequestParam("suppliers") MultipartFile suppliers) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		supSer.uploadSuppliers(suppliers, merchant, user.getEmail());
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletResponse response) throws Exception {
		String content = "Name1,Email1,Mobile1,GSTIN1,Addr Line1,Addr Line2,city,district,state code,pincode,country\r\n"
				+ "Name2,Email2,Mobile2,GSTIN2,Addr Line1,Addr Line2,city,district,04,pincode,India"
				+ "Name3,Email3,Mobile3,GSTIN3,Addr Line1,Addr Line2,city,district,27,pincode,India";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkSupplier.csv\"");
		response.setContentType("application/csv");
		response.getOutputStream().write(content.getBytes());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all", method = RequestMethod.GET)
	public List<BulkSupplierUpload> uploadSuppliers() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return supSer.getUploads(merchant);
	}

	@RequestMapping(value = "/bulk/download/{accessKey}/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String accessKey, @PathVariable String filename) throws IOException {
		return supSer.downloadFile(accessKey, filename);
	}
}
