package com.personal.kopmorning.domain.admin.responseCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AdminErrorCode {
    SUSPEND_DAYS_IS_NULL("404", "정지 기간이 존재하지 않습니다.");

    private String code;
    private String message;

    private AdminErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
