package com.paycr.merchant.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.UpdateConsumerRequest;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkConsumerUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.ConsumerService;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

	@Autowired
	private ConsumerService conSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/new")
	public void newConsumer(@RequestBody Consumer consumer) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.newConsumer(consumer, merchant, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{consumerId}")
	public void updateConsumer(@RequestBody Consumer consumer, @PathVariable Integer consumerId) {
		conSer.updateConsumer(consumer, consumerId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/address/update/{consumerId}")
	public void updateConsumerAddress(@RequestBody Address address, @PathVariable Integer consumerId) {
		conSer.updateConsumerAddress(address, consumerId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/category/new/{consumerId}")
	public void addCategory(@RequestBody ConsumerCategory conCat, @PathVariable Integer consumerId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.addCategory(consumerId, conCat, merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/category/delete/{consumerId}/{conCatId}")
	public void deleteCategory(@PathVariable Integer consumerId, @PathVariable Integer conCatId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.deleteCategory(consumerId, conCatId, merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/categories")
	public List<String> getCategories() {
		return conSer.getCategories();
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/category/{category}")
	public List<String> getCategoryValues(@PathVariable String category) {
		return conSer.getCategoryValues(category);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/category")
	public void updateConsumerCategory(@RequestBody UpdateConsumerRequest updateReq) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.updateConsumerCategory(updateReq, merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload", method = RequestMethod.POST)
	public void uploadConsumers(@RequestParam("consumers") MultipartFile consumers) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.uploadConsumers(consumers, merchant, user.getEmail());
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "Name1*,Email1*,Mobile1*,GSTIN1,Category Name, Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,YES(copyBillAddrToShipAddr)\r\n"
				+ "Name2*,Email2*,Mobile2*,GSTIN2,Category Name,Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,NO\r\n"
				+ "Name3*,Email3*,Mobile3*,GSTIN3,Category Name,Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,YES\r\n";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkConsumer.csv\"");
		response.setContentType("application/csv");
		response.getOutputStream().write(content.getBytes());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all", method = RequestMethod.GET)
	public List<BulkConsumerUpload> allUploads() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return conSer.getUploads(merchant);
	}

	@RequestMapping(value = "/bulk/download/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String filename) throws IOException {
		return conSer.downloadFile(filename);
	}
}
