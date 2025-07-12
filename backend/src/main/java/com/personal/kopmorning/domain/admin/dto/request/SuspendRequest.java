package com.personal.kopmorning.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuspendRequest {
    private Long memberId;
    private Long suspendDays;
    private String status;
    private String reason;
}
