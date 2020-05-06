package com.paycr.oauth.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class Company {

	@Value("${company.url.web}")
	private String webUrl;

	@Value("${company.url.oauth}")
	private String oauthUrl;

	@Value("${company.contact.name}")
	private String contactName;

	@Value("${company.contact.email}")
	private String contactEmail;

	@Value("${company.contact.password}")
	private String contactPassword;

}
