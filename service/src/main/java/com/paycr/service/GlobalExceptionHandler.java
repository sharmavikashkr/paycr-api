package com.paycr.service;

import java.io.IOException;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.paycr.common.exception.ApiErrorResponse;
import com.paycr.common.exception.PaycrException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = { PaycrException.class, ConstraintViolationException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse paycrException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return new ApiErrorResponse(400, ex.getMessage());
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class, IOException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiErrorResponse noHandlerFoundException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return new ApiErrorResponse(404, "Resource Not Found");
	}

	@ExceptionHandler(value = { HttpClientErrorException.class, AccessDeniedException.class,
			UserRedirectRequiredException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiErrorResponse unauthorizedException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return new ApiErrorResponse(401, "Unauthorized");
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiErrorResponse unknownException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return new ApiErrorResponse(500, "Internal Server Error");
	}
}
