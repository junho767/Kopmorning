package com.personal.kopmorning.domain.article.article.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticleListResponse {
    private List<ArticleResponse> articles;
    private int total;
    private String category;
}
