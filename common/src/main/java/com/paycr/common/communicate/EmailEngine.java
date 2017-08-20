package com.paycr.common.communicate;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.paycr.common.bean.Company;
import com.paycr.common.util.RestTemplateUtil;

@Component
public class EmailEngine {

	@Value("${email.mailgun.apiKey}")
	private String mailgunApiKey;

	@Value("${email.mailgun.host}")
	private String mailgunHost;

	@Value("${email.mailgun.domain}")
	private String mailgunDomain;

	@Autowired
	private Company company;

	@Async
	@SuppressWarnings("deprecation")
	public void send(Email email) {
		try {
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("api", mailgunApiKey));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setCredentialsProvider(credentialsProvider);
			ClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(httpClient);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<String, String>();
			dataMap.add("from", company.getName() + " <" + email.getFrom() + ">");
			dataMap.add("to", email.getTo().get(0));
			dataMap.add("subject", email.getSubject());
			dataMap.add("html", email.getMessage());
			HttpEntity<Object> input = new HttpEntity<Object>(dataMap, header);
			RestTemplate rest = RestTemplateUtil.getRestTemplate();
			rest.setRequestFactory(rf);
			ResponseEntity<Map> resp = rest.exchange(URI.create(mailgunHost + mailgunDomain + "/messages"),
					HttpMethod.POST, input, Map.class);
		} catch (Exception ex) {
		}
	}

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
			message.setContent(email.getMessage(), "text/html; charset=utf-8");
			Transport.send(message);
		} catch (Exception e) {
		}
	}
}
