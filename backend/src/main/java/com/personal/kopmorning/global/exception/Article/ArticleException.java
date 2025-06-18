package com.personal.kopmorning.global.exception.Article;

import com.personal.kopmorning.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ArticleException extends ServiceException {
    public ArticleException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }
}
