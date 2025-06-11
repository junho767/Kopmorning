package com.personal.kopmorning.global.exception.member;

import com.personal.kopmorning.global.exception.security.SecurityException;

public class MemberNotFoundException extends SecurityException {
    public MemberNotFoundException(String code, String message) {
        super(code, message);
    }
}
