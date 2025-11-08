package com.personal.kopmorning.domain.member.responseCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode {
    MEMBER_UNAUTHENTICATED("401-1", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_MISSING("403-1", "토큰이 존재하지 않습니다.", HttpStatus.FORBIDDEN),
    TOKEN_INVALID("403-2", "잘못된 토큰 인증 정보 입니다.", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRE("403-3", "만료된 토큰 입니다.", HttpStatus.FORBIDDEN),
    TOKEN_REFRESH_EXPIRE("403-4", "리프레시 토큰 만료-다시 로그인해주세요.", HttpStatus.FORBIDDEN),

    MEMBER_NOT_FOUND("404-1", "존재하지 않는 회원 입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    MemberErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code.split("-")[0];
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
