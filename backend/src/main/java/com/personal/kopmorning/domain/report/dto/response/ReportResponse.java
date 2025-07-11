package com.personal.kopmorning.domain.report.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.personal.kopmorning.domain.report.entity.Report;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse {
    private Long reportId;
    private Long articleId;
    private Long commentId;
    private Long memberId;
    private String reason;
    private String reportDate;

    public ReportResponse(Report report) {
        this.reportId = report.getId();
        this.articleId = report.getArticle().getId();
        this.commentId = report.getArticleComment().getId();
        this.memberId = report.getMember().getId();
        this.reason = report.getReason();
        this.reportDate = report.getReportedAt().toString();
    }
}
