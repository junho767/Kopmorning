package com.personal.kopmorning.domain.admin.controller;

import com.personal.kopmorning.domain.admin.dto.request.RollUpdateRequest;
import com.personal.kopmorning.domain.admin.dto.request.SuspendRequest;
import com.personal.kopmorning.domain.admin.service.AdminService;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.service.ArticleService;
import com.personal.kopmorning.domain.article.comment.service.ArticleCommentService;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.report.dto.response.ReportResponse;
import com.personal.kopmorning.domain.report.service.ReportService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final ReportService reportService;
    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;

    @PatchMapping("/roll")
    public RsData<?> updateMemberRoll(@RequestBody RollUpdateRequest requestDTO) {
        adminService.updateRoll(requestDTO);
        return new RsData<>(
                "200",
                "admin"
        );
    }

    @PatchMapping("/suspend")
    public RsData<?> updateMemberSuspend(@RequestBody SuspendRequest requestDTO) {
        adminService.updateMemberSuspend(requestDTO);
        return new RsData<>(
                "200",
                "admin"

        );
    }

    @GetMapping("/member/list")
    public RsData<List<MemberResponse>> getMemberList(
    ) {
        return new RsData<>(
                "200",
                "admin",
                adminService.getMemberList()
        );
    }

    @GetMapping("/article/list/{category}")
    public RsData<ArticleListResponse> getArticleList(@PathVariable(required = false) String category) {
        return new RsData<>(
                "200",
                "admin",
                adminService.getArticleList(category)
        );
    }

    @GetMapping("/report/list")
    public RsData<List<ReportResponse>> getReportList() {
        return new RsData<>(
                  "200",
                "admin",
                reportService.getList()
        );
    }

    @DeleteMapping("/article/{articleId}")
    public RsData<?> deleteArticle(@PathVariable Long articleId) {
        articleService.forceDeleteArticle(articleId);
        return new RsData<>(
                "200",
                "admin"
        );
    }

    @DeleteMapping("/comment/{commentId}")
    public RsData<?> deleteComment(@PathVariable Long commentId) {
        articleCommentService.forceDeleteComment(commentId);
        return new RsData<>("200", "admin");
    }
}
