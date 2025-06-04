package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // todo : 탈퇴한 유저라면 어떤 식으로 클라이언트에게 표현해야 하는 지 고민해야 할 필요가 있음.
    @PatchMapping("/delete/cancel")
    public RsData<?> cancelDelete() {
        memberService.deleteCancel();
        return new RsData<>(
                "200",
                "탈퇴 처리 취소 성공"
        );
    }

    @PatchMapping("/delete")
    public RsData<?> deleteMember() {
        memberService.deleteRequest();
        return new RsData<>(
                "200",
                "탈퇴 성공 - 7일 내 계정 부활 가능"
        );
    }
}