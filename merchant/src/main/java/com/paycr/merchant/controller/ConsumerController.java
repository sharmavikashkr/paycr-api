package com.paycr.merchant.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
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
	public void newConsumer(@RequestBody Consumer consumer, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			conSer.newConsumer(consumer, merchant, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{consumerId}")
	public void updateConsumer(@RequestBody Consumer consumer, @PathVariable Integer consumerId,
			HttpServletResponse response) {
		try {
			conSer.updateConsumer(consumer, consumerId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/address/update/{consumerId}")
	public void updateConsumerAddress(@RequestBody Address address, @PathVariable Integer consumerId,
			HttpServletResponse response) {
		try {
			conSer.updateConsumerAddress(address, consumerId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/category/new/{consumerId}")
	public void addCategory(@RequestBody ConsumerCategory conCat, @PathVariable Integer consumerId,
			HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			conSer.addCategory(consumerId, conCat, merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/category/delete/{consumerId}/{conCatId}")
	public void deleteCategory(@PathVariable Integer consumerId, @PathVariable Integer conCatId,
			HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			conSer.deleteCategory(consumerId, conCatId, merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/categories")
	public List<String> getCategories(HttpServletResponse response) {
		return conSer.getCategories();
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/category/{category}")
	public List<String> getCategoryValues(@PathVariable String category, HttpServletResponse response) {
		return conSer.getCategoryValues(category);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/category")
	public void updateConsumerCategory(@RequestBody UpdateConsumerRequest updateReq, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			conSer.updateConsumerCategory(updateReq, merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload", method = RequestMethod.POST)
	public void uploadConsumers(@RequestParam("consumers") MultipartFile consumers, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			conSer.uploadConsumers(consumers, merchant, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "Name1*,Email1*,Mobile1*,GSTIN1,Category Name, Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,YES(copyBillAddrToShipAddr)\r\n"
				+ "Name2*,Email2*,Mobile2*,GSTIN2,Category Name,Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,NO\r\n"
				+ "Name3*,Email3*,Mobile3*,GSTIN3,Category Name,Category Value,Bill Addr Line1,Bill Addr Line2,Bill City,Bill State,Bill Pincode,Bill Country,YES\r\n";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkConsumer.csv\"");
		response.setContentType("application/csv");
		InputStream is = new ByteArrayInputStream(content.getBytes());
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all", method = RequestMethod.GET)
	public List<BulkConsumerUpload> uploadConsumers(HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return conSer.getUploads(merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@RequestMapping(value = "/bulk/download/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String filename, HttpServletResponse response) {
		try {
			return conSer.downloadFile(filename);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}
}
