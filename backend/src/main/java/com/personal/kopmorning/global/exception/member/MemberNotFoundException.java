package com.personal.kopmorning.global.exception.member;

public class MemberNotFoundException extends SecurityException {
    public MemberNotFoundException(String code, String message) {
        super(code, message);
    }
}
