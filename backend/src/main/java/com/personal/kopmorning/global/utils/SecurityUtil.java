package com.personal.kopmorning.global.utils;

import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.global.security.PrincipalDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    // 현재 인증된 사용자 아이디 반환
    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new SecurityException("authentication is not authenticated");
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getMember().getId();
    }

    // 현재 인증된 사용자 반환
    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            throw new SecurityException("authentication is not authenticated");
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getMember();
    }
}