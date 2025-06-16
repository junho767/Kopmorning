package com.personal.kopmorning.domain.article.controller;

import com.personal.kopmorning.domain.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.entity.Article;
import com.personal.kopmorning.domain.article.service.ArticleService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public RsData<ArticleResponse> createArticle(@RequestBody ArticleCreate articleCreate) {
        return new RsData<>(
                "200",
                "게시물 생성 성공",
                articleService.addArticle(articleCreate)
        );
    }
}
