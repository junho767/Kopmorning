package com.personal.kopmorning.domain.admin.dto.request;

import lombok.Data;

@Data
public class RollUpdateRequest {
    private Long memberId;
    private String role;
}
