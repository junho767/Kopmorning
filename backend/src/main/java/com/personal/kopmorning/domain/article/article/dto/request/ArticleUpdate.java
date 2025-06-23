package com.personal.kopmorning.domain.article.article.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleUpdate {
    private String title;
    private String body;
}
