package com.personal.kopmorning.domain.member.responseCode;

import lombok.Getter;

@Getter
public enum MemberErrorCode {
    ERROR_CODE("404-1", "실패"),
    TOKEN_MISSING("401-1", "토큰이 존재하지 않습니다."),
    MEMBER_NOT_FOUND("404-1", "존재하지 않는 회원 입니다.");

    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
        this.code = code.substring(2);
        this.message = message;
    }
}
