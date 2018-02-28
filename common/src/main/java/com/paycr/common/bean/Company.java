package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Company {

	@Value("${company.name}")
	private String name;

	@Value("${company.email}")
	private String email;

	@Value("${company.mobile}")
	private String mobile;

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

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPassword() {
		return contactPassword;
	}

	public void setContactPassword(String contactPassword) {
		this.contactPassword = contactPassword;
	}

	public String getStaticUrl() {
		return staticUrl;
	}

	public void setStaticUrl(String staticUrl) {
		this.staticUrl = staticUrl;
	}

	public String getOauthUrl() {
		return oauthUrl;
	}

	public void setOauthUrl(String oauthUrl) {
		this.oauthUrl = oauthUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

}
