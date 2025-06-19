package com.personal.kopmorning.global.utils;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.security.PrincipalDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    // 현재 인증된 사용자 아이디 반환 - 반드시 필요한 경우
    public static Long getRequiredMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new MemberException(
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getCode(),
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getMessage(),
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getHttpStatus()
            );
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getMember().getId();
    }

    // 현재 인증된 사용자 아이디 반환 - 없어도 괜찮은 경우
    public static Long getNullableMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            return null;
        }
        return ((PrincipalDetails) authentication.getPrincipal()).getMember().getId();
    }

    // 현재 인증된 사용자 반환
    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new MemberException(
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getCode(),
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getMessage(),
                    MemberErrorCode.MEMBER_UNAUTHENTICATED.getHttpStatus()
            );
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getMember();
    }
}