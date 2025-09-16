package com.personal.kopmorning.domain.admin.responseCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AdminErrorCode {
    LOGIN_FAIL("403", "관리자 로그인 실패", HttpStatus.FORBIDDEN),
    SUSPEND_DAYS_IS_NULL("404", "정지 기간이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private String code;
    private String message;
    private HttpStatus httpStatus;

    private AdminErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
