package com.paycr.common.communicate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.gson.Gson;
import com.paycr.common.bean.AwsConfig;

@Component
public class SmsEngine {

	private static final Logger logger = LoggerFactory.getLogger(SmsEngine.class);

	@Autowired
	private AwsConfig awsConfig;

	@Async
	public void sendViaSNS(Sms sms) {
		try {
			AWSCredentialsProvider credentials = new AWSCredentialsProviderChain(new AWSStaticCredentialsProvider(
					new BasicAWSCredentials(awsConfig.getAccessKey(), awsConfig.getSecretKey())));
			AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withCredentials(credentials).build();
			String message = sms.getMessage();
			String phoneNumber = "+91" + sms.getTo();
			Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
			PublishResult result = snsClient.publish(new PublishRequest().withMessage(message)
					.withPhoneNumber(phoneNumber).withMessageAttributes(smsAttributes));
			logger.info("SMS sent : {} with result : {}", new Gson().toJson(sms), new Gson().toJson(result));
		} catch (Exception ex) {
			logger.error("SMS sending exception : ", ex);
		}
	}
}
