package com.personal.kopmorning.domain.report.repository;

import com.personal.kopmorning.domain.report.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 첫 페이지: 커서 없는 경우, 최신 보고서부터
    List<Report> findAllByOrderByReportedAtDesc(Pageable pageable);

    // 다음 페이지: 커서 있는 경우, cursor 이전 보고서, 최신순
    List<Report> findByIdLessThanOrderByReportedAtDesc(Long cursor, Pageable pageable);
}
