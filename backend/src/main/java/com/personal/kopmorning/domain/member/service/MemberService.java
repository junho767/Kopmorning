package com.personal.kopmorning.domain.member.service;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.jwt.TokenService;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    // 회원 정보 수정 메서드
    @Transactional
    public void update(MemberProfileUpdate request) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        if (member != null) {
            member.setNickname(request.getNickname());
            member.setUpdated_at(LocalDateTime.now());
        }
    }

    public Member getMemberBySecurityMember() {
        return SecurityUtil.getCurrentMember();
    }

    public void logout(String refreshToken) {
        long expirationTime = tokenService.getExpirationTimeFromToken(refreshToken);
        tokenService.addToBlacklist(refreshToken, expirationTime);
    }

    // 회원탈퇴 신청
    @Transactional
    public void deleteRequest() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        member.withdraw();
    }

    // 회원탈퇴 철회
    @Transactional
    public void deleteCancel() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));
        member.isActive();
    }
}
