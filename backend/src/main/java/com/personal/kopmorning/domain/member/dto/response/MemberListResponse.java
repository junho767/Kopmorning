package com.personal.kopmorning.domain.member.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MemberListResponse {
    private int totalMembers;
    private Long nextCursor;
    private List<MemberResponse> memberResponses;

    public MemberListResponse(int totalMembers, Long nextCursor, List<MemberResponse> memberResponses) {
        this.totalMembers = totalMembers;
        this.nextCursor = nextCursor;
        this.memberResponses = memberResponses;
    }
}
