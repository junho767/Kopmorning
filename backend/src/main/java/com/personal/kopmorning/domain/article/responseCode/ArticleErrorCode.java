package com.personal.kopmorning.domain.article.responseCode;

import lombok.Getter;

@Getter
public enum ArticleErrorCode {
    INVALID_ARTICLE("404", "존재하지 않는 게시물 입니다."),
    NOT_AUTHOR("403", "글에 대한 권한이 존재하지 않습니다.");

    private final String code;
    private final String message;

    ArticleErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
