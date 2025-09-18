package com.personal.kopmorning.domain.article.comment.dto.response;

import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleCommentResponse {
    private Long id;
    private Long memberId;
    private Long articleId;
    private String body;
    private String author;
    private LocalDateTime createAt;

    public ArticleCommentResponse(ArticleComment articleComment) {
        this.id = articleComment.getId();
        this.articleId = articleComment.getArticle().getId();
        this.memberId = articleComment.getMember().getId();
        this.body = articleComment.getBody();
        this.author = articleComment.getMember().getName();
        this.createAt = articleComment.getCreatedAt();
    }
}
