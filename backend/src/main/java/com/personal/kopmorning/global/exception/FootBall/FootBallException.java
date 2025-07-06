package com.personal.kopmorning.global.exception.FootBall;

import com.personal.kopmorning.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class FootBallException extends ServiceException {
    public FootBallException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }
}
