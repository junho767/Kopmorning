package com.personal.kopmorning.global.exception.security;

import com.personal.kopmorning.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class TokenException extends ServiceException {
    public TokenException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }
}
