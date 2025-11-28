package com.personal.kopmorning.domain.admin.controller;

import com.personal.kopmorning.domain.admin.dto.request.RollUpdateRequest;
import com.personal.kopmorning.domain.admin.dto.request.SuspendRequest;
import com.personal.kopmorning.domain.admin.responseCode.AdminSuccessCode;
import com.personal.kopmorning.domain.admin.service.AdminService;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.service.ArticleService;
import com.personal.kopmorning.domain.article.comment.dto.response.ArticleCommentResponse;
import com.personal.kopmorning.domain.article.comment.service.ArticleCommentService;
import com.personal.kopmorning.domain.member.dto.response.MemberListResponse;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.domain.report.dto.response.ReportListResponse;
import com.personal.kopmorning.domain.report.service.ReportService;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.scheduler.FootBallScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final MemberService memberService;
    private final ReportService reportService;
    private final ArticleService articleService;
    private final FootBallScheduler footBallScheduler;
    private final ArticleCommentService articleCommentService;

    @GetMapping("/football/status")
    public RsData<Object> getFootballSchedulerStatus() {
        return new RsData<>(
                AdminSuccessCode.GET_SCHEDULER_STATUS.getCode(),
                AdminSuccessCode.GET_SCHEDULER_STATUS.getMessage(),
                footBallScheduler.getCurrentStatus()
        );
    }

    @PatchMapping("/roll")
    public RsData<?> updateMemberRoll(@RequestBody RollUpdateRequest requestDTO) {
        adminService.updateRoll(requestDTO);
        return new RsData<>(
                AdminSuccessCode.MODIFY_ROLL.getCode(),
                AdminSuccessCode.MODIFY_ROLL.getMessage()
        );
    }

    @PatchMapping("/suspend")
    public RsData<?> updateMemberSuspend(@RequestBody SuspendRequest requestDTO) {
        adminService.updateMemberSuspend(requestDTO);
        return new RsData<>(
                AdminSuccessCode.SUSPEND_MEMBER.getCode(),
                AdminSuccessCode.SUSPEND_MEMBER.getMessage()
        );
    }

    @GetMapping("/member/list")
    public RsData<MemberListResponse> getMemberList(
            @RequestParam(required = false) Long nextCursor,
            @RequestParam(defaultValue = "5") int size
    ) {
        return new RsData<>(
                AdminSuccessCode.GET_MEMBER_LIST_BY_ADMIN.getCode(),
                AdminSuccessCode.GET_MEMBER_LIST_BY_ADMIN.getMessage(),
                memberService.getMemberList(nextCursor, size)
        );
    }

    @GetMapping("/article/list/{category}")
    public RsData<ArticleListResponse> getArticleList(
            @PathVariable(required = false) String category,
            @RequestParam(required = false) Long nextCursor,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new RsData<>(
                AdminSuccessCode.GET_ARTICLE_LIST_BY_ADMIN.getCode(),
                AdminSuccessCode.GET_ARTICLE_LIST_BY_ADMIN.getMessage(),
                adminService.getArticleList(category, nextCursor, size, keyword)
        );
    }

    @GetMapping("/comment/list")
    public RsData<List<ArticleCommentResponse>> getCommentList() {
        return new RsData<>(
                AdminSuccessCode.GET_COMMENT_LIST.getCode(),
                AdminSuccessCode.GET_COMMENT_LIST.getMessage(),
                adminService.getCommentList()
        );
    }

    @GetMapping("/report/list")
    public RsData<ReportListResponse> getReportList(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new RsData<>(
                AdminSuccessCode.GET_REPORT_LIST.getCode(),
                AdminSuccessCode.GET_REPORT_LIST.getMessage(),
                reportService.getList(cursor, size)
        );
    }

    @DeleteMapping("/article/{articleId}")
    public RsData<?> deleteArticle(@PathVariable Long articleId) {
        articleService.forceDeleteArticle(articleId);
        return new RsData<>(
                AdminSuccessCode.DELETE_ARTICLE_FORCE.getCode(),
                AdminSuccessCode.DELETE_ARTICLE_FORCE.getMessage()
        );
    }

    @DeleteMapping("/comment/{commentId}")
    public RsData<?> deleteComment(@PathVariable Long commentId) {
        articleCommentService.forceDeleteComment(commentId);
        return new RsData<>(
                AdminSuccessCode.DELETE_COMMENT_FORCE.getCode(),
                AdminSuccessCode.DELETE_COMMENT_FORCE.getMessage()
        );
    }
}
