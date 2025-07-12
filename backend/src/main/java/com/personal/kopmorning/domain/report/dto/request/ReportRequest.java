package com.personal.kopmorning.domain.report.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportRequest {
    private Long id;
    private String reason;
}
