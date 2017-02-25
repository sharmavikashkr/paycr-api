package com.payme.common.communicate;

import java.util.List;

public class Email {

	private String from;
	private List<String> to;
	private List<String> cc;
	private String subject;
	private String message;

	public Email() {
	}

	public Email(String from, List<String> to, List<String> cc) {
		this.from = from;
		this.to = to;
		this.cc = cc;
	}

	public Email(String from, List<String> to, List<String> cc, String subject, String message) {
		this.from = from;
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
}
