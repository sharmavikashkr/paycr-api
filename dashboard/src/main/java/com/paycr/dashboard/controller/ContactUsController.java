package com.paycr.dashboard.controller;

import java.util.List;

import javax.validation.Valid;

import com.paycr.common.data.domain.ContactUs;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ContactUsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

	@Autowired
	private ContactUsService cntUsSer;

	@PostMapping("/new")
	public void contactUs(@Valid @RequestBody ContactUs contactUs) {
		cntUsSer.contactUs(contactUs);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADVISOR_AUTH)
	@PostMapping("/search")
	public List<ContactUs> get(@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "resolved", required = false) boolean resolved, @RequestParam("page") Integer page) {
		page = page - 1;
		return cntUsSer.getContactUs(email, type, page, resolved);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADVISOR_AUTH)
	@GetMapping("/toggle/{id}")
	public void get(@PathVariable Integer id) {
		cntUsSer.toggle(id);
	}

}
