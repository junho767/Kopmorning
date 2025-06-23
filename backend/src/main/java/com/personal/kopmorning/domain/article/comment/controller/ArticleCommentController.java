package com.personal.kopmorning.domain.article.comment.controller;

import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentCreate;
import com.personal.kopmorning.domain.article.comment.dto.request.ArticleCommentUpdate;
import com.personal.kopmorning.domain.article.comment.dto.response.ArticleCommentResponse;
import com.personal.kopmorning.domain.article.comment.service.ArticleCommentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/article/comment")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final ArticleCommentService articleCommentService;

    @GetMapping("/{articleId}")
    public RsData<List<ArticleCommentResponse>> getCommentList(@PathVariable Long articleId) {
        return new RsData<>("200", "조회 성공",  articleCommentService.getList(articleId));
    }

    @PostMapping
    public RsData<?> createComment(@RequestBody ArticleCommentCreate articleCommentCreate) {
        return new RsData<>("200", "댓글 작성 성공", articleCommentService.create(articleCommentCreate));
    }

    @PatchMapping
    public RsData<?> updateComment(@RequestBody ArticleCommentUpdate articleCommentUpdate) {
        articleCommentService.update(articleCommentUpdate);
        return new RsData<>("200", "댓글 수정 성공");
    }

    @DeleteMapping("/{commentId}")
    public RsData<?> deleteComment(@PathVariable Long commentId) {
        articleCommentService.delete(commentId);
        return new RsData<>("200", "댓글 삭제 성공");
    }
}
