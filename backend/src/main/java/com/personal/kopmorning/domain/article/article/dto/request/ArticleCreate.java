package com.personal.kopmorning.domain.article.article.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleCreate {
    private String title;
    private String body;
    private String category;
}
