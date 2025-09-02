package com.personal.kopmorning.domain.report.controller;

import com.personal.kopmorning.domain.report.dto.request.ReportRequest;
import com.personal.kopmorning.domain.report.service.ReportService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/article")
    public RsData<?> reportArticle(@RequestBody ReportRequest request) {
        reportService.article(request);
        return new RsData<>(
                "200",
                "신고 접수"
        );
    }

    @PostMapping("/comment")
    public RsData<?> reportComment(@RequestBody ReportRequest request) {
        reportService.comment(request);
        return new RsData<>(
                "200",
                "신고 접수"
        );
    }
}
