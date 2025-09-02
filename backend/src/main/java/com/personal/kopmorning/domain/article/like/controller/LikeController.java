package com.personal.kopmorning.domain.article.like.controller;

import com.personal.kopmorning.domain.article.responseCode.ArticleSuccessCode;
import com.personal.kopmorning.domain.article.like.service.LikeService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService articleLikeService;

    @PostMapping("/article/{articleId}")
    public RsData<?> likeArticle(@PathVariable Long articleId) {
        boolean isLiked = articleLikeService.handleArticleLike(articleId);

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

    @PostMapping("/comment/{commentId}")
    public RsData<?> likeComment(@PathVariable Long commentId) {
        boolean isLiked = articleLikeService.handleArticleCommentLike(commentId);

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
