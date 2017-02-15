package compayme.common.email;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class SessionGenerator {

	Session getMailSession(EmailAuthentication Sender) {
		final EmailAuthentication sender = Sender;
		Session session = Session.getInstance(sender.getJavaMailProperties(),
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(sender.getUsername(),
								sender.getPassword());
					}
				});
		return session;
	}

}
