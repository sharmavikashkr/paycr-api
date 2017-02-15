package compayme.common.email;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Email
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "netcore.mail")
public class EmailAuthentication extends JavaMailSenderImpl {

	private Properties javaMailProperties = new Properties();
	private String host;
	private String username;
	private String password;
	private String debug;
	private String smtpAuth;
	private String smtpStarttlsEnable;
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
