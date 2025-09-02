package com.personal.kopmorning.domain.article.responseCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArticleErrorCode {
    INVALID_ARTICLE("404-1", "존재하지 않는 게시물 입니다.", HttpStatus.NOT_FOUND),
    INVALID_COMMENT("404-2", "존재하지 않는 댓글 입니다.", HttpStatus.NOT_FOUND),
    NOT_AUTHOR("403-1", "작성자가 아닙니다.", HttpStatus.FORBIDDEN),;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ArticleErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code.split("-")[0];
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
