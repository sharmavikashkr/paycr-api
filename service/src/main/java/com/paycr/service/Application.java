package com.paycr.service;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication(scanBasePackages = { "com.paycr" })
@EnableAutoConfiguration
@EntityScan(basePackages = { "com.paycr.common.data.domain" })
@EnableJpaRepositories(basePackages = { "com.paycr.common.data.repository" })
@EnableAsync
@EnableScheduling
@PropertySource(value = { "classpath:application.properties",
		"classpath:local.properties" }, ignoreResourceNotFound = true)
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.addListener(new RequestContextListener());
	}

}