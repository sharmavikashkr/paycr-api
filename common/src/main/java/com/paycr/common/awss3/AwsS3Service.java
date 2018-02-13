package com.paycr.common.awss3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class AwsS3Service {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);

	@Autowired
	private AmazonS3Client awsS3Client;

	@Autowired
	private AwsS3Config awsS3Config;

	public void createBucket() {
		if (bucketExist()) {
			logger.info("Bucket already exist : {}", awsS3Config.getBucketName());
		} else {
			awsS3Client.createBucket(awsS3Config.getBucketName());
			logger.info("Bucket created with name : {}", awsS3Config.getBucketName());
		}
	}

	public void createFolder(String folderName) {
		createBucket();
		if (!folderExist(folderName)) {
			logger.info("Creating folder : " + folderName);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
			PutObjectRequest putObjectRequest = new PutObjectRequest(awsS3Config.getBucketName(), folderName + "/",
					emptyContent, metadata);
			awsS3Client.putObject(putObjectRequest);
		}
	}

	public String saveFile(String folderName, File file) {
		createFolder(folderName);
		String fileName = folderName.concat("/").concat(file.getName());
		awsS3Client.putObject(new PutObjectRequest(awsS3Config.getBucketName(), fileName, file));
		file.delete();
		return getAmazonS3Url(folderName, file.getName());
	}

	public byte[] getFile(String folderName, String fileName) throws IOException {
		S3Object obj = awsS3Client
				.getObject(new GetObjectRequest(awsS3Config.getBucketName(), folderName.concat("/").concat(fileName)));
		return IOUtils.toByteArray(obj.getObjectContent());
	}

	private boolean bucketExist() {
		List<Bucket> bucketList = awsS3Client.listBuckets();
		for (Bucket bucket : bucketList) {
			if (bucket.getName().equals(awsS3Config.getBucketName())) {
				return true;
			}
		}
		logger.info("Bucket with name : {} doesn't exist", awsS3Config.getBucketName());
		return false;
	}

	private boolean folderExist(String folderName) {
		try {
			awsS3Client.getObject(new GetObjectRequest(awsS3Config.getBucketName(), folderName.concat("/")));
			logger.info("Bucket : {} already contains folder : {}", awsS3Config.getBucketName(), folderName);
		} catch (AmazonServiceException e) {
			return false;
		}
		return true;
	}

	public String getAmazonS3Url(String folderName, String fileName) {
		return awsS3Config.getDomainUrl().concat("/").concat(awsS3Config.getBucketName()).concat("/").concat(folderName)
				.concat("/").concat(fileName);
	}

}
