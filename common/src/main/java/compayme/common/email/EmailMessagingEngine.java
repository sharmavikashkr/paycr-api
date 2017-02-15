package compayme.common.email;

import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Email
public class EmailMessagingEngine {

	private final static Logger logger = LoggerFactory
			.getLogger(EmailMessagingEngine.class);

	private EmailAuthentication sender;
	private Message email;

	@Autowired
	public EmailMessagingEngine(@Email EmailAuthentication sender) {
		this.sender = sender;
	}

	public void send(String from, String[] tos, String subject, String message,
			String[] attachments) {
		SessionGenerator sessionGenerator = new SessionGenerator();
		EmailConfigurer emailConfigurer = new EmailConfigurer();
		Session session = sessionGenerator.getMailSession(sender);
		email = emailConfigurer.configureEmailWithAttachment(session, tos,
				from, subject, message, attachments);
		try {
			Transport.send(email);
			logger.info("verification mail sent to {} ", Arrays.asList(tos));
		} catch (MessagingException e) {
			logger.info("email send failed: ", e.getMessage());
		}
	}
}
