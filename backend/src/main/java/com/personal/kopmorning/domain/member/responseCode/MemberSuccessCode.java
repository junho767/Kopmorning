package com.personal.kopmorning.domain.member.responseCode;

import lombok.Getter;

@Getter
public enum MemberSuccessCode {
    GET_MEMBER("200-1", "유저 정보 조회 성공"),
    UPDATE_MEMBER("200-2", "유저 정보 수정 성공"),
    LOGOUT("200-3", "로그아웃 성공"),
    DELETE_REQUEST("200-4", "회원탈퇴 성공"),
    DELETE_CANCEL("200-5", "회원탈퇴 취소 성공");

    private final String code;
    private final String message;

    MemberSuccessCode(String code, String message) {
        this.code = code.substring(2);
        this.message = message;
    }
}
