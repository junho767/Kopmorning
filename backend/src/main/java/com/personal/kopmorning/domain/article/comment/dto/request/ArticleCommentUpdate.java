package com.personal.kopmorning.domain.article.comment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleCommentUpdate {
    private Long articleCommentId;
    private String body;
}
