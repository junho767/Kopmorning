package com.personal.kopmorning.domain.article.dto.response;

import com.personal.kopmorning.domain.article.entity.Article;
import com.personal.kopmorning.domain.member.entity.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {
    private Long id;
    private String title;
    private String body;
    private Long likeCount;
    private Member member;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.body = article.getBody();
        this.likeCount = article.getLikeCount();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.category = article.getCategory().name();
        this.member = article.getMember();
    }
}
