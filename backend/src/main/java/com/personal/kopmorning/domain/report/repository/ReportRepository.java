package com.personal.kopmorning.domain.report.repository;

import com.personal.kopmorning.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
