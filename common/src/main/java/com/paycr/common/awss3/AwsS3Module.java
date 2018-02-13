package com.paycr.common.awss3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.paycr.common.bean.AwsConfig;

@Component
public class AwsS3Module {

	@Autowired
	private AwsConfig awsConfig;

	@SuppressWarnings("deprecation")
	@Bean
	public AmazonS3Client amazonS3Client() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(awsConfig.getAccessKey(), awsConfig.getSecretKey());
		return new AmazonS3Client(awsCredentials);
	}
}
