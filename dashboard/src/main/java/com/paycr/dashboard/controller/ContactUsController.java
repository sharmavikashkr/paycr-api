package com.paycr.dashboard.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.ContactUs;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ContactUsService;

@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

	@Autowired
	private ContactUsService cntUsSer;

	@Autowired
	private Company company;

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView contactUs(@Valid ContactUs contactUs) {
		cntUsSer.contactUs(contactUs);
		ModelAndView mv = new ModelAndView("html/contactus-success");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
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
