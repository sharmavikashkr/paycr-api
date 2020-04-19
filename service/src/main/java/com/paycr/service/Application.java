package com.paycr.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = { "com.paycr" })
@EntityScan(basePackages = { "com.paycr.common.data.domain" })
@EnableJpaRepositories(basePackages = { "com.paycr.common.data.repository" })
@EnableAsync
@PropertySource(value = { "classpath:application.properties",
		"classpath:local.properties" }, ignoreResourceNotFound = true)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}