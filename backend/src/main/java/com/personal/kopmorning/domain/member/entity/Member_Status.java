package com.personal.kopmorning.domain.member.entity;

public enum Member_Status {
    ACTIVE("active"),       // 정상 회원
    INACTIVE("inactive"),   // 휴먼 회원
    SUSPEND("suspend"),     // 정지 회원
    DELETED("deleted");     // 탈퇴 회원

    Member_Status(String active) {
    }
}
