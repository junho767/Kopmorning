package com.personal.kopmorning.domain.member.service;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
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

    // 회원 정보 수정 메서드
    @Transactional
    public void update(MemberProfileUpdate request) {
        // todo: 예외처리 똑바로 하자
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new RuntimeException("멤버가 존재하지 않습니다."));

        if (member != null) {
            member.setNickname(request.getNickname());
            member.setUpdated_at(LocalDateTime.now());
        }
    }

    public Member getMemberBySecurityMember() {
        return SecurityUtil.getCurrentMember();
    }
}
