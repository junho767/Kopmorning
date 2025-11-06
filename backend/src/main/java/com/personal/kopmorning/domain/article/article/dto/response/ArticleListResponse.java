package com.personal.kopmorning.domain.article.article.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ArticleListResponse {
    private List<ArticleResponse> articles;
    private int total;
    private Long nextCursor;
    private String category;

    @Builder
    public ArticleListResponse(List<ArticleResponse> articles, String category, Long nextCursor) {
        this.articles = articles;
        this.total = articles.size();
        this.nextCursor = nextCursor;
        this.category = category;
    }
}
