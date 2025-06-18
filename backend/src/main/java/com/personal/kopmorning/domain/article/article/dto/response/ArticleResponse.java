package com.personal.kopmorning.domain.article.article.dto.response;

import com.personal.kopmorning.domain.article.article.entity.Article;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {
    private Long id;
    private String title;
    private String body;
    private Long likeCount;
    private Long viewCount;
    private Long member_id;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.body = article.getBody();
        this.likeCount = article.getLikeCount();
        this.viewCount = article.getViewCount();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.category = article.getCategory().name();
        this.member_id = article.getMember().getId();
    }
}
