package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Company {

	@Value("${company.name}")
	private String name;

	@Value("${company.baseUrl}")
	private String baseUrl;

	@Value("${company.contact}")
	private String contact;

	@Value("${company.env}")
	private String env;

	@Value("${company.webClient}")
	private String webClient;

	@Value("${company.webSecret}")
	private String webSecret;

	@Value("${company.mobClient}")
	private String mobClient;

	@Value("${company.mobSecret}")
	private String mobSecret;

	public String getWebClient() {
		return webClient;
	}

	public void setWebClient(String webClient) {
		this.webClient = webClient;
	}

	public String getWebSecret() {
		return webSecret;
	}

	public void setWebSecret(String webSecret) {
		this.webSecret = webSecret;
	}

	public String getMobClient() {
		return mobClient;
	}

	public void setMobClient(String mobClient) {
		this.mobClient = mobClient;
	}

	public String getMobSecret() {
		return mobSecret;
	}

	public void setMobSecret(String mobSecret) {
		this.mobSecret = mobSecret;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
