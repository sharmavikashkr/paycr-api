package com.paycr.common.validation;

public interface RequestValidator<T> {

	public void validate(T t);

}
