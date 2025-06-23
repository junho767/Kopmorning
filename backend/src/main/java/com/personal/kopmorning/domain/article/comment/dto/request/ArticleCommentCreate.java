package com.personal.kopmorning.domain.article.comment.dto.request;

import lombok.Data;

@Data
public class ArticleCommentCreate {
    private Long articleId;
    private String body;
}
