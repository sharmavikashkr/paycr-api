package com.paycr.common.awss3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class AwsS3Config {

	@Value("${aws.s3.bucketName}")
	private String bucketName;

	@Value("${aws.s3.domainUrl}")
	private String domainUrl;

}
