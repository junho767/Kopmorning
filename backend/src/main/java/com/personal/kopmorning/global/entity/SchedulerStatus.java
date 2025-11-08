package com.personal.kopmorning.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulerStatus {
    private String jobName;
    private String status;
    private String lastStartedAt;
    private String lastFinishedAt;
    private Long durationMs;
    private String errorMessage;
    private String runId;
}
