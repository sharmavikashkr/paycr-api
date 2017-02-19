package com.payme.common.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Email
@Component
public class EmailAuthentication extends JavaMailSenderImpl {

	private Properties javaMailProperties = new Properties();
	
	@Value("${netcore.mail.host}")
	private String host;
	
	@Value("${netcore.mail.username}")
	private String username;
	
	@Value("${netcore.mail.password}")
	private String password;
	
	@Value("${netcore.mail.debug}")
	private String debug;
	
	@Value("${netcore.mail.smtpAuth}")
	private String smtpAuth;
	
	@Value("${netcore.mail.smtpStarttlsEnable}")
	private String smtpStarttlsEnable;
	
	@Value("${netcore.mail.smtpPort}")
	private String smtpPort;

	public EmailAuthentication() {
		super();
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public String getSmtpStarttlsEnable() {
		return smtpStarttlsEnable;
	}

	public void setSmtpStarttlsEnable(String smtpStarttlsEnable) {
		this.smtpStarttlsEnable = smtpStarttlsEnable;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Properties getJavaMailProperties() {
		javaMailProperties.setProperty("mail.smtp.auth", smtpAuth);
		javaMailProperties.setProperty("mail.debug", debug);
		javaMailProperties.setProperty("mail.smtp.starttls.enable", smtpStarttlsEnable);
		javaMailProperties.setProperty("mail.smtp.host", host);
		javaMailProperties.setProperty("mail.smtp.port", smtpPort);
		return javaMailProperties;
	}

	public String getHost() {
		return host;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
