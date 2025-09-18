package com.personal.kopmorning.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberStatus {
    ACTIVE("일반 회원"),    // 정상 회원
    INACTIVE("휴먼 회원"),  // 휴먼 회원
    SUSPEND("정지 회원"),   // 정지 회원
    DELETED("탈퇴 회원"),   // 탈퇴 회원
    ADMIN("관리자");       // 관리자

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }
}
