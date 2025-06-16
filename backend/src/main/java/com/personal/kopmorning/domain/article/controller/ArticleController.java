package com.personal.kopmorning.domain.article.controller;

import com.personal.kopmorning.domain.article.dto.request.ArticleCreate;
import com.personal.kopmorning.domain.article.dto.request.ArticleUpdate;
import com.personal.kopmorning.domain.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.service.ArticleService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/{id}")
    public RsData<ArticleResponse> getArticleOne(@PathVariable Long id) {
        return new RsData<>(
                "200",
                "단건 조회 성공",
                articleService.getArticleOne(id));
    }

    @GetMapping("/list/{category}")
    public RsData<?> getArticleListByCategory(@PathVariable String category) {
        return new RsData<>(
                "200",
                "다건 조회 성공",
                articleService.getArticleListByCategory(category)
        );
    }

    @PostMapping
    public RsData<ArticleResponse> createArticle(@RequestBody ArticleCreate articleCreate) {
        return new RsData<>(
                "200",
                "게시물 생성 성공",
                articleService.addArticle(articleCreate)
        );
    }

    @PatchMapping("/{id}")
    public RsData<?> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdate articleUpdate) {
        articleService.updateArticle(id, articleUpdate);
        return new RsData<>(
                "200",
                "게시물 수정 성공"
        );
    }

    @DeleteMapping("/{id}")
    public RsData<?> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new RsData<>(
                "200",
                "게시물 삭제 성공"
        );
    }
}
