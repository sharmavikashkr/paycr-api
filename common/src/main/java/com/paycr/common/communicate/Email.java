package com.paycr.common.communicate;

import java.util.List;

public class Email {

	private String name;
	private String from;
	private String password;
	private List<String> to;
	private List<String> cc;
	private String subject;
	private String message;

	public Email() {
	}

	public Email(String name, String from, String password, List<String> to, List<String> cc) {
		this.name = name;
		this.from = from;
		this.password = password;
		this.to = to;
		this.cc = cc;
	}

	public Email(String name, String from, String password, List<String> to, List<String> cc, String subject,
			String message) {
		this.name = name;
		this.from = from;
		this.password = password;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
