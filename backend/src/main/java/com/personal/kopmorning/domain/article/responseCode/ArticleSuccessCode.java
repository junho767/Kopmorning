package com.personal.kopmorning.domain.article.responseCode;

import lombok.Getter;

@Getter
public enum ArticleSuccessCode {
    CREATE("200", "게시물 생성 성공"),
    UPDATE("200", "게시물 수정 성공"),
    DELETE("200", "게시물 삭제 성공"),
    GET_ONE("200", "게시물 단건 조회 성공"),
    GET_LIST("200", "게시물 다건 조회 성공");

    private final String code;
    private final String message;

    ArticleSuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
