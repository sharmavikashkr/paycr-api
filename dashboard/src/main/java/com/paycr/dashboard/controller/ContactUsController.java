package com.paycr.dashboard.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.ContactUs;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ContactUsService;

@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

	@Autowired
	private ContactUsService cntUsSer;

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public void contactUs(@Valid @RequestBody ContactUs contactUs) {
		cntUsSer.contactUs(contactUs);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADVISOR_AUTH)
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public List<ContactUs> get(@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "resolved", required = false) boolean resolved, @RequestParam("page") Integer page) {
		page = page - 1;
		return cntUsSer.getContactUs(email, type, page, resolved);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADVISOR_AUTH)
	@RequestMapping("/toggle/{id}")
	public void get(@PathVariable Integer id) {
		cntUsSer.toggle(id);
	}

}
