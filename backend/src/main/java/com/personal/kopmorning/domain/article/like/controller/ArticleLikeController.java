package com.personal.kopmorning.domain.article.like.controller;

import com.personal.kopmorning.domain.article.like.service.ArticleLikeService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article/like")
@RequiredArgsConstructor
public class ArticleLikeController {
    private final ArticleLikeService articleLikeService;

    @PostMapping("/{articleId}")
    public RsData<?> likeArticle(@PathVariable Long articleId) {
        articleLikeService.handleLike(articleId);
        return new RsData<>(
                "200",
                "좋아요 기능 수행 완료"
        );
    }
}
