package com.personal.kopmorning.domain.admin.responseCode;

import lombok.Getter;

@Getter
public enum AdminSuccessCode {
    MODIFY_ROLL("200", "권한 변경 성공"),
    SUSPEND_MEMBER("200", "활동 정지 부여 성공"),
    GET_MEMBER_LIST_BY_ADMIN("200", "회원 목록 조회"),
    GET_ARTICLE_LIST_BY_ADMIN("200", "게시물 목록 조회 성공"),
    GET_REPORT_LIST("200", "신고 목록 조회"),
    DELETE_ARTICLE_FORCE("200", "게시물 강제 삭제 성공"),
    DELETE_COMMENT_FORCE("200", "댓글 강제 삭제 성공");

    private final String code;
    private final String message;

    AdminSuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
