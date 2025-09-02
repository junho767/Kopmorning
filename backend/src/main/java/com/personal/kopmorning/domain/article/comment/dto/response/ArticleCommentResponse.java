package com.personal.kopmorning.domain.article.comment.dto.response;

import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import lombok.Data;

@Data
public class ArticleCommentResponse {
    private Long id;
    private Long articleId;
    private String body;
    private String author;

    public ArticleCommentResponse(ArticleComment articleComment) {
        this.id = articleComment.getId();
        this.articleId = articleComment.getArticle().getId();
        this.body = articleComment.getBody();
        this.author = articleComment.getMember().getName();
    }
}
