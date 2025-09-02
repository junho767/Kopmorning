package com.personal.kopmorning.domain.article.article.dto.response;

import com.personal.kopmorning.domain.article.article.entity.Article;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {
    private Long id;
    private Long likeCount;
    private Long viewCount;
    private Long member_id;

    private String title;
    private String body;
    private String category;

    private boolean likedByMember;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArticleResponse(Article article, boolean checkLike) {
        this.id = article.getId();
        this.likeCount = article.getLikeCount();
        this.viewCount = article.getViewCount();
        this.member_id = article.getMember().getId();

        this.title = article.getTitle();
        this.body = article.getBody();
        this.category = article.getCategory().name();

        this.likedByMember = checkLike;

        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.likeCount = article.getLikeCount();
        this.viewCount = article.getViewCount();
        this.member_id = article.getMember().getId();

        this.title = article.getTitle();
        this.body = article.getBody();
        this.category = article.getCategory().name();

        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }
}
