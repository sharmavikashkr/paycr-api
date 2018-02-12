package com.paycr.common.communicate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.paycr.common.util.RestTemplateUtil;

@Component
public class SmsEngine {

	private static final Logger logger = LoggerFactory.getLogger(SmsEngine.class);

	@Value("${sms.textlocal.apiKey}")
	private String textlocalApiKey;

	@Value("${sms.textlocal.host}")
	private String textlocalHost;

	@Async
	public void sendViaTL(Sms sms) {
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
		} catch (Exception ex) {
			logger.error("Execption while sending sms to : {} ", sms.getTo(), ex);
		}
	}

	@SuppressWarnings("deprecation")
	@Async
	public void sendViaSNS(Sms sms) {
		AWSCredentialsProvider credentials = new AWSCredentialsProviderChain(new StaticCredentialsProvider(
				new BasicAWSCredentials("AKIAIYURUPFR4KJRO3TA", "lhdtWyF7RprMm44AQxC4xkI8oSaXRuP0V4VojWAo")));
		AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withCredentials(credentials).build();
		String message = sms.getMessage();
		String phoneNumber = "+91" + sms.getTo();
		Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
		PublishResult result = snsClient.publish(new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber)
				.withMessageAttributes(smsAttributes));
		System.out.println(result);
	}
}
