package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping
    public RsData<MemberResponse> getMember() {
        return new RsData<>(
                "200",
                "현재 로그인한 유저 정보 반환 성공",
                new MemberResponse(memberService.getMemberBySecurityMember())
        );
    }

    @PatchMapping
    public RsData<?> updateMember(@RequestBody MemberProfileUpdate request) {
        memberService.update(request);

        return new RsData<>(
                "200",
                "수정 완료"
        );
    }
}