package com.payme.common.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailConfigurer {

	private final static Logger logger = LoggerFactory
			.getLogger(EmailConfigurer.class);

	MimeMessage configureHtmlEmail(Session session, String to, String from,
			String subject, String message) {
		MimeMessage email = new MimeMessage(session);
		try {
			MimeMessageHelper helper = new MimeMessageHelper(email, true);
			helper.setTo(InternetAddress.parse(to));
			helper.setSubject(subject);
			if (from != null && !from.isEmpty()) {
				helper.setFrom(new InternetAddress(from));
			}
			helper.setSubject(subject);
			helper.setText(message, true);
		} catch (AddressException e) {
			logger.info("invalid address: ", e.getMessage());
		} catch (MessagingException e) {
			logger.info("email send failed: ", e.getMessage());
		}
		return email;

	}

	Message configureTextEmail(Session session, String to, String from,
			String subject, String message) {
		Message email = new MimeMessage(session);
		try {
			email.setFrom(new InternetAddress(from));
			email.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			email.setSubject(subject);
			email.setText(message);
		} catch (AddressException e) {
			logger.info("invalid address: ", e.getMessage());
		} catch (MessagingException e) {
			logger.info("email send failed: ", e.getMessage());
		}
		return email;
	}

	MimeMessage configureEmailWithAttachment(Session session, String[] tos,
			String from, String subject, String message, String[] attachments) {
		MimeMessage email = new MimeMessage(session);
		try {
			MimeMessageHelper helper = new MimeMessageHelper(email, true);
			for (String to : tos) {
				helper.addTo(to);
			}
			helper.setSubject(subject);
			if (from != null && !from.isEmpty()) {
				helper.setFrom(new InternetAddress(from));
			}
			helper.setSubject(subject);
			helper.setText(message, true);
			for (String fileName : attachments) {
				FileSystemResource file = new FileSystemResource(fileName);
				helper.addAttachment(file.getFilename(), file);
			}
		} catch (AddressException e) {
			logger.info("invalid address ", e.getMessage());
		} catch (MessagingException e) {
			logger.info("email send failed ", e.getMessage());
		}
		return email;
	}
}
