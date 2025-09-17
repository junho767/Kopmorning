package com.personal.kopmorning.domain.report.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.article.responseCode.ArticleErrorCode;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.domain.report.dto.request.ReportRequest;
import com.personal.kopmorning.domain.report.dto.response.ReportListResponse;
import com.personal.kopmorning.domain.report.dto.response.ReportResponse;
import com.personal.kopmorning.domain.report.entity.Report;
import com.personal.kopmorning.domain.report.repository.ReportRepository;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public void article(ReportRequest request) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        Article article = articleRepository.findById(request.getId())
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );
        Report report = new Report(article, member, request.getReason());

        reportRepository.save(report);
    }


    public void comment(ReportRequest request) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        ArticleComment comment = articleCommentRepository.findById(request.getId())
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_COMMENT.getCode(),
                                ArticleErrorCode.INVALID_COMMENT.getMessage(),
                                ArticleErrorCode.INVALID_COMMENT.getHttpStatus()
                        )
                );
        Report report = new Report(comment, member, request.getReason());

        reportRepository.save(report);
    }

    public ReportListResponse getList(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Report> reports;

        if (cursor == null) {
            reports = reportRepository.findAllByOrderByReportedAtDesc(pageable);
        } else {
            reports = reportRepository.findByIdLessThanOrderByReportedAtDesc(cursor, pageable);
        }

        int totalReports = reports.size();

        List<ReportResponse> reportResponses = reports.stream()
                .map(ReportResponse::new)
                .toList();

        Long nextCursor = reportResponses.isEmpty() ? null : reportResponses.getLast().getId();

        return new ReportListResponse(reportResponses, totalReports, nextCursor);
    }
}
