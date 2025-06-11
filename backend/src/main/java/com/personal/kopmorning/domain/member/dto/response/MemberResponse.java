package com.personal.kopmorning.domain.member.dto.response;

import com.personal.kopmorning.domain.member.entity.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole().name();
        this.createdAt = member.getCreated_at();
        this.updatedAt = LocalDateTime.now();
    }
}
