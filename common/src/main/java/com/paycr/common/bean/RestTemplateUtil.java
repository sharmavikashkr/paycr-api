package com.paycr.common.bean;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {

	private static int connectionTimeout = 10;
	private static int readTimeout = 10;

	public static RestTemplate getRestTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(connectionTimeout * 1000);
		requestFactory.setReadTimeout(readTimeout * 1000);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

	public static RestTemplate getRestTemplate(int connectionTimeoutinSeconds, int readTimeoutinSeconds) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(connectionTimeoutinSeconds * 1000);
		requestFactory.setReadTimeout(readTimeoutinSeconds * 1000);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		return restTemplate;
	}

}
