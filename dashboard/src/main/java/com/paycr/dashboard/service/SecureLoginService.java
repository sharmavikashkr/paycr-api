package com.paycr.dashboard.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.paycr.common.bean.Company;
import com.paycr.common.client.RestTemplateClient;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;

@Service
public class SecureLoginService {

	@Autowired
	private Company company;

	public LinkedHashMap secureLogin(String email, String password) {
		try {
			RestTemplate restTemplate = RestTemplateClient.getRestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			header.add("Authorization", "Basic d2ViLWNsaWVudDozYjVlOGViM2ZjZmFmYTJlN2IzMDJmNzVjMGUxODVkMzNkODY5MGMy");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("username", email);
			map.add("password", password);
			map.add("grant_type", "password");
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, header);
			ResponseEntity<LinkedHashMap> response = restTemplate.exchange(company.getOauthUrl() + "/oauth/token",
					HttpMethod.POST, httpEntity, LinkedHashMap.class);
			return response.getBody();
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Something went wrong");
		}
	}

}
