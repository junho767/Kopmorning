package com.personal.kopmorning.domain.member.responseCode;

import lombok.Getter;

@Getter
public enum MemberSuccessCode {
    GET_MEMBER_INFO("200-1", "유저 정보 조회 성공"),
    LOGOUT_SUCCESS("200-2", "로그아웃 성공");

    private final String code;
    private final String message;

    MemberSuccessCode(String code, String message) {
        this.code = code.substring(2);
        this.message = message;
    }
}
