package com.paycr.common.communicate;

import java.net.URI;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.paycr.common.bean.Company;
import com.paycr.common.util.RestTemplateUtil;

@Component
public class EmailEngine {

	@Value("${email.mailgun.apiKey}")
	private String mailgunApiKey;

	@Value("${email.mailgun.host}")
	private String mailgunHost;

	@Value("${email.mailgun.domain}")
	private String mailgunDomain;

	@Autowired
	private Company company;

	@SuppressWarnings("deprecation")
	public boolean send(Email email) {
		try {
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("api", mailgunApiKey));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setCredentialsProvider(credentialsProvider);
			ClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(httpClient);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<String, String>();
			dataMap.add("from", company.getName() + " <" + email.getFrom() + ">");
			dataMap.add("to", email.getTo().get(0));
			dataMap.add("subject", email.getSubject());
			dataMap.add("html", email.getMessage());
			HttpEntity<Object> input = new HttpEntity<Object>(dataMap, header);
			RestTemplate rest = RestTemplateUtil.getRestTemplate();
			rest.setRequestFactory(rf);
			ResponseEntity<Map> resp = rest.exchange(URI.create(mailgunHost + mailgunDomain + "/messages"),
					HttpMethod.POST, input, Map.class);
			if (HttpStatus.OK.equals(resp.getStatusCode())) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}
}
