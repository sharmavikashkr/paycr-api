package com.payme.common.validation;

public interface RequestValidator<T> {

	public void validate(T t);

}
