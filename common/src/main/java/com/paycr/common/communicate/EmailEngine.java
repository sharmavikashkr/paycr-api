package com.paycr.common.communicate;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.paycr.common.util.CommonUtil;

@Component
public class EmailEngine {

	@Async
	public void sendViaGmail(Email email) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email.getFrom(), email.getPassword());
			}
		});
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo().get(0)));
			message.setSubject(email.getSubject());
			message.setFrom(new InternetAddress(email.getFrom(), email.getName()));
			message.setContent(getContent(email));
			Transport.send(message);
		} catch (Exception e) {
		}
	}

	private Multipart getContent(Email email) throws MessagingException {
		BodyPart messageBody = new MimeBodyPart();
		messageBody.setContent(email.getMessage(), "text/html; charset=utf-8");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBody);
		if (!CommonUtil.isEmpty(email.getFileName())) {
			MimeBodyPart attachment = new MimeBodyPart();
			DataSource source = new FileDataSource(email.getFilePath());
			attachment.setDataHandler(new DataHandler(source));
			attachment.setFileName(email.getFileName());
			multipart.addBodyPart(attachment);
		}
		return multipart;
	}
}
