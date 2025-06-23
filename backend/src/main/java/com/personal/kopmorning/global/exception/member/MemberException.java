package com.personal.kopmorning.global.exception.member;

import com.personal.kopmorning.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class MemberException extends ServiceException {
    public MemberException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }
}
