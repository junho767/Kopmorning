package com.personal.kopmorning.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public ServiceException(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
