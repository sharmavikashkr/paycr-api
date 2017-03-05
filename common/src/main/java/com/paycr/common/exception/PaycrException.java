package com.paycr.common.exception;

@SuppressWarnings("serial")
public class PaycrException extends RuntimeException {

	private final int status;
	private final String message;

	public PaycrException(int status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
