package com.personal.kopmorning.domain.article.comment.controller;

import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentCreate;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentUpdate;
import com.personal.kopmorning.domain.article.comment.dto.response.CommentsResponse;
import com.personal.kopmorning.domain.article.comment.service.ArticleCommentService;
import com.personal.kopmorning.domain.article.responseCode.ArticleSuccessCode;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article/comment")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final ArticleCommentService articleCommentService;

    @GetMapping("/{articleId}")
    public RsData<CommentsResponse> getCommentList(
            @PathVariable Long articleId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new RsData<>(
                ArticleSuccessCode.GET_COMMENT.getCode(),
                ArticleSuccessCode.GET_COMMENT.getMessage(),
                articleCommentService.getList(articleId, cursor, size)
        );
    }

    @PostMapping
    public RsData<?> createComment(@RequestBody ArticleCommentCreate articleCommentCreate) {
        return new RsData<>(
                ArticleSuccessCode.CREATE_COMMENT.getCode(),
                ArticleSuccessCode.CREATE_COMMENT.getMessage(),
                articleCommentService.create(articleCommentCreate));
    }

    @PatchMapping
    public RsData<?> updateComment(@RequestBody ArticleCommentUpdate articleCommentUpdate) {
        articleCommentService.update(articleCommentUpdate);
        return new RsData<>(
                ArticleSuccessCode.UPDATE_COMMENT.getCode(),
                ArticleSuccessCode.UPDATE_COMMENT.getMessage()
        );
    }

    @DeleteMapping("/{commentId}")
    public RsData<?> deleteComment(@PathVariable Long commentId) {
        articleCommentService.delete(commentId);
        return new RsData<>(
                ArticleSuccessCode.DELETE_COMMENT.getCode(),
                ArticleSuccessCode.DELETE_COMMENT.getMessage()
        );
    }
}
