package com.personal.kopmorning.domain.report.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReportListResponse {
    private int totalReports;
    private Long nextCursor;
    private List<ReportResponse> reportResponses;

    public ReportListResponse(List<ReportResponse> reportResponses, int totalReports, Long nextCursor) {
        this.totalReports = totalReports;
        this.nextCursor = nextCursor;
        this.reportResponses = reportResponses;
    }
}
