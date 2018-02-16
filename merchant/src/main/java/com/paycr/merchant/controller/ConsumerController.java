package com.paycr.merchant.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import com.paycr.common.bean.UpdateConsumerRequest;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.BulkConsumerUpload;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
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
	public void newConsumer(@Valid @RequestBody Consumer consumer) {
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
	public void updateConsumerAddress(@Valid @RequestBody Address address, @PathVariable Integer consumerId) {
		conSer.updateConsumerAddress(address, consumerId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/flag/new/{consumerId}")
	public void addFlag(@Valid @RequestBody ConsumerFlag flag, @PathVariable Integer consumerId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.addFlag(consumerId, flag, merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/flag/delete/{consumerId}/{flagId}")
	public void deleteFlag(@PathVariable Integer consumerId, @PathVariable Integer flagId) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.deleteFlag(consumerId, flagId, merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/flags")
	public List<String> getFlags() {
		return conSer.getFlags();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/flag")
	public void updateConsumerFlag(@RequestBody UpdateConsumerRequest updateReq) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		conSer.updateConsumerFlag(updateReq, merchant);
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
		String content = "Name1*,Email1*,Mobile1*,GSTIN1,Flag Name,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State Code,Bill Pincode,Bill Country,YES(copyBillAddrToShipAddr)\r\n"
				+ "Name2*,Email2*,Mobile2*,GSTIN2,Flag Name,Bill Addr Line1,Bill Addr Line2,Bill City,04,Bill Pincode,India,NO\r\n"
				+ "Name3*,Email3*,Mobile3*,GSTIN3,Flag Name,Bill Addr Line1,Bill Addr Line2,Bill City,27,Bill Pincode,India,YES\r\n";
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

	@RequestMapping(value = "/bulk/download/{accessKey}/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String accessKey, @PathVariable String filename) throws IOException {
		return conSer.downloadFile(accessKey, filename);
	}
}
