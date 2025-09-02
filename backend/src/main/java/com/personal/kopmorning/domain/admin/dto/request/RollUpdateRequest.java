package com.personal.kopmorning.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RollUpdateRequest {
    private Long memberId;
    private String role;
}
