package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final CookieUtil cookieUtil;
    private final MemberService memberService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @GetMapping
    public RsData<MemberResponse> getMember() {
        return new RsData<>(
                "200",
                "현재 로그인한 유저 정보 반환 성공",
                new MemberResponse(memberService.getMemberBySecurityMember())
        );
    }

    @GetMapping("/login")
    public String login() {
        return "localhost:8080/oauth2/authorization/google";
    }

    @PostMapping("/logout")
    public RsData<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String accessToken = null;

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            accessToken = authHeader.substring(7);
        }

        if (accessToken == null) {
            accessToken = cookieUtil.getAccessTokenFromCookie(request);
        }

        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            return new RsData<>(
                    "200",
                    "토큰이 없어요"
            );
        }

        memberService.logout(refreshToken);

        cookieUtil.removeAccessTokenFromCookie(response);
        cookieUtil.removeRefreshTokenFromCookie(response);

        return new RsData<>(
                "200",
                "로그아웃 성공 [쿠키에서 토큰 제거]"
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