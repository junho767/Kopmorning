package com.personal.kopmorning.domain.member.responseCode;

import lombok.Getter;

@Getter
public enum MemberErrorCode {
    TOKEN_MISSING("401-1", "토큰이 존재하지 않습니다."),
    TOKEN_INVALID("401-2", "잘못된 인증 정보 입니다."),
    TOKEN_EXPIRE("403-1", "만료된 토큰 입니다."),
    TOKEN_REFRESH_EXPIRE("403-2", "다시 로그인해주세요."),
    MEMBER_NOT_FOUND("404-1", "존재하지 않는 회원 입니다.");

    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
        this.code = code.split("-")[0];
        this.message = message;
    }
}
