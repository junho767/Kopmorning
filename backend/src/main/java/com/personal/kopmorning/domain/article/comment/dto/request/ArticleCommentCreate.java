package com.personal.kopmorning.domain.article.comment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleCommentCreate {
    private Long articleId;
    private String body;
}
