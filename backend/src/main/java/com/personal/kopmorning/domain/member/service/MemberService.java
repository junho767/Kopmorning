package com.personal.kopmorning.domain.member.service;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberListResponse;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.jwt.TokenService;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    // todo: 목록 조회 조건 설정 해야 합니다.
    public MemberListResponse getMemberList(Long nextCursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Member> memberList = memberRepository.findAll();

        int totalMembers = memberList.size();

        if (nextCursor == null) {
            memberList = memberRepository.findAllByOrderByIdDesc(pageable);
        } else {
            memberList = memberRepository.findByIdLessThanOrderByIdDesc(nextCursor, pageable);
        }

        List<MemberResponse> memberResponses = memberList.stream()
                .map(MemberResponse::new)
                .toList();

        Long newCursor = memberResponses.isEmpty() ? null : memberResponses.getLast().getId();

        return new MemberListResponse(totalMembers, newCursor, memberResponses);
    }

    public Member getCurrentMember() {
        return memberRepository.findById(SecurityUtil.getCurrentMember().getId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));
    }

    // 회원 정보 수정 메서드
    @Transactional
    public void update(MemberProfileUpdate request) {
        Member member = getCurrentMember();
        if (member != null) {
            member.setNickname(request.getNickname());
            member.setUpdated_at(LocalDateTime.now());
        }
    }

    public void logout(String accessToken, String refreshToken) {
        long accessTokenExpirationTime = tokenService.getRemainingTime(accessToken);
        long refreshTokenExpirationTime = tokenService.getRemainingTime(refreshToken);
        tokenService.addToBlacklist(accessToken, refreshToken, accessTokenExpirationTime, refreshTokenExpirationTime);
    }

    // 회원탈퇴 신청
    @Transactional
    public void deleteRequest() {
        Member member = getCurrentMember();
        member.withdraw();
    }

    // 회원탈퇴 철회
    @Transactional
    public void deleteCancel() {
        Member member = getCurrentMember();
        member.isActive();
    }

    // 즉시 회원탈퇴
    public void deleteImmediately() {
        Member member = getCurrentMember();
        memberRepository.deleteById(member.getId());
    }

    public Member getMemberBySecurityMember() {
        return SecurityUtil.getCurrentMember();
    }
}
