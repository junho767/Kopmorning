package com.personal.kopmorning.domain.article.article.controller;

import com.personal.kopmorning.domain.article.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.article.dto.request.ArticleUpdate;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.responseCode.ArticleSuccessCode;
import com.personal.kopmorning.domain.article.article.service.ArticleService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/{id}")
    public RsData<ArticleResponse> getArticleOne(@PathVariable Long id) {
        return new RsData<>(
                ArticleSuccessCode.GET_ONE.getCode(),
                ArticleSuccessCode.GET_ONE.getMessage(),
                articleService.getArticleOne(id));
    }

    @GetMapping("/list/{category}")
    public RsData<?> getArticleListByCategory(@PathVariable String category) {
        return new RsData<>(
                ArticleSuccessCode.GET_LIST.getCode(),
                ArticleSuccessCode.GET_LIST.getMessage(),
                articleService.getArticleListByCategory(category)
        );
    }

    @PostMapping
    public RsData<ArticleResponse> createArticle(@RequestBody ArticleCreate articleCreate) {
        return new RsData<>(
                ArticleSuccessCode.CREATE.getCode(),
                ArticleSuccessCode.CREATE.getMessage(),
                articleService.addArticle(articleCreate)
        );
    }

    @PatchMapping("/{id}")
    public RsData<?> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdate articleUpdate) {
        articleService.updateArticle(id, articleUpdate);
        return new RsData<>(
                ArticleSuccessCode.UPDATE.getCode(),
                ArticleSuccessCode.UPDATE.getMessage()
        );
    }

    @DeleteMapping("/{id}")
    public RsData<?> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new RsData<>(
                ArticleSuccessCode.DELETE.getCode(),
                ArticleSuccessCode.DELETE.getMessage()
        );
    }
}
