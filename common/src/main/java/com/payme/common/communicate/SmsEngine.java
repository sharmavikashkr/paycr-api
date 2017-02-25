package com.payme.common.communicate;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.payme.common.bean.RestTemplateUtil;

@Component
public class SmsEngine {

	@Value("${sms.textlocal.apiKey}")
	private String textlocalApiKey;

	@Value("${sms.textlocal.host}")
	private String textlocalHost;

	public boolean send(Sms sms) {
		try {
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<String, String>();
			dataMap.add("username", "sharma.vikashkr@gmail.com");
			dataMap.add("hash", textlocalApiKey);
			dataMap.add("message", sms.getMessage());
			dataMap.add("sender", "PAYKR");
			dataMap.add("numbers", sms.getTo());
			HttpEntity<Object> input = new HttpEntity<Object>(dataMap, header);
			RestTemplate rest = RestTemplateUtil.getRestTemplate(25, 25);
			ResponseEntity<Map> resp = rest.exchange(URI.create(textlocalHost), HttpMethod.POST, input, Map.class);
			if (HttpStatus.OK.equals(resp.getStatusCode())) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}
}
