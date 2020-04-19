package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class AwsConfig {

	@Value("${aws.config.accessKey}")
	private String accessKey;

	@Value("${aws.config.secretKey}")
	private String secretKey;

	@Value("${aws.smtp.host}")
	private String smtpHost;

	@Value("${aws.smtp.username}")
	private String smtpUsername;

	@Value("${aws.smtp.password}")
	private String smtpPassword;

}
