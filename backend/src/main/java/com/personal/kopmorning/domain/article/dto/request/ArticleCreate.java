package com.personal.kopmorning.domain.article.dto.request;

import lombok.Data;

@Data
public class ArticleCreate {
    private String title;
    private String body;
    private String category;
}
