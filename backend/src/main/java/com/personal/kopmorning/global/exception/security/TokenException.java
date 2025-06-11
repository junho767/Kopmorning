package com.personal.kopmorning.global.exception.security;

public class TokenException extends SecurityException {
    public TokenException(String code, String message) {
        super(code, message);
    }
}
