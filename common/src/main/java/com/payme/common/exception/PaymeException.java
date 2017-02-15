package com.payme.common.exception;

@SuppressWarnings("serial")
public class PaymeException extends RuntimeException {

	private final int status;
	private final String message;

	public PaymeException(int status, String message) {
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
