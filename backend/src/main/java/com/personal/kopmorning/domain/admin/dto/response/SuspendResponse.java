package com.personal.kopmorning.domain.admin.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SuspendResponse implements Serializable {
    private Long memberId;
    private LocalDateTime suspendedUntil;
    private String reason;
}
