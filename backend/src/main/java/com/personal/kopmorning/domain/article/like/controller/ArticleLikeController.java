package com.personal.kopmorning.domain.article.like.controller;

import com.personal.kopmorning.domain.article.article.responseCode.ArticleSuccessCode;
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
        boolean isLiked = articleLikeService.handleLike(articleId);

        if (isLiked) {
            return new RsData<>(
                    ArticleSuccessCode.ADD_LIKE.getCode(),
                    ArticleSuccessCode.ADD_LIKE.getMessage()
            );
        } else {
            return new RsData<>(
                    ArticleSuccessCode.CANCEL_LIKE.getCode(),
                    ArticleSuccessCode.CANCEL_LIKE.getMessage()
            );
        }
    }
}
