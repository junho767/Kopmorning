package com.personal.kopmorning.domain.member.entity;

public enum Member_Status {
    NEW("new"),             // 신규 회원
    ACTIVE("active"),       // 정상 회원
    INACTIVE("inactive"),   // 휴먼 회원
    DELETED("deleted");     // 탈퇴 회원

    Member_Status(String active) {
    }
}
