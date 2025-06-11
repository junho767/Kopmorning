package com.personal.kopmorning.global.exception.security;

import com.personal.kopmorning.global.exception.ServiceException;

public abstract class SecurityException extends ServiceException {
    public SecurityException(String code, String message) {
        super(code, message);
    }
}
