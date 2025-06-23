package com.personal.kopmorning.domain.article.comment.dto.request;

import lombok.Data;

@Data
public class ArticleCommentUpdate {
    private Long articleCommentId;
    private String body;
}
