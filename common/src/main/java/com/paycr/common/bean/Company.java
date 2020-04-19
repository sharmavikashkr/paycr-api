package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class Company {

	@Value("${company.merchant.id}")
	private Integer merchantId;

	@Value("${company.app.url}")
	private String appUrl;

	@Value("${company.web.url}")
	private String webUrl;

	@Value("${company.static.url}")
	private String staticUrl;

	@Value("${company.oauth.url}")
	private String oauthUrl;

	@Value("${company.contact.name}")
	private String contactName;

	@Value("${company.contact.email}")
	private String contactEmail;

	@Value("${company.contact.password}")
	private String contactPassword;

}
