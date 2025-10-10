package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.dto.request.MemberProfileUpdate;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.domain.member.responseCode.MemberSuccessCode;
import com.personal.kopmorning.domain.member.service.MemberService;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
                MemberSuccessCode.GET_MEMBER.getCode(),
                MemberSuccessCode.GET_MEMBER.getMessage(),
                new MemberResponse(memberService.getMemberBySecurityMember())
        );
    }

    @PostMapping("/logout")
    public RsData<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtil.getAccessTokenFromCookie(request);
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            return new RsData<>(
                    MemberErrorCode.TOKEN_MISSING.getCode(),
                    MemberErrorCode.TOKEN_MISSING.getMessage()
            );
        }

        memberService.logout(accessToken, refreshToken);

        cookieUtil.removeAccessTokenFromCookie(response);
        cookieUtil.removeRefreshTokenFromCookie(response);

        return new RsData<>(
                MemberSuccessCode.LOGOUT.getCode(),
                MemberSuccessCode.LOGOUT.getMessage()
        );
    }

    @PatchMapping
    public RsData<?> updateMember(@RequestBody MemberProfileUpdate request) {
        memberService.update(request);

        return new RsData<>(
                MemberSuccessCode.UPDATE_MEMBER.getCode(),
                MemberSuccessCode.UPDATE_MEMBER.getMessage()
        );
    }

    // todo : 탈퇴한 유저라면 어떤 식으로 클라이언트에게 표현해야 하는 지 고민해야 할 필요가 있음.
    @PatchMapping("/delete/cancel")
    public RsData<?> deleteCancel() {
        memberService.deleteCancel();
        return new RsData<>(
                MemberSuccessCode.DELETE_CANCEL.getCode(),
                MemberSuccessCode.DELETE_CANCEL.getMessage()
        );
    }

    @PatchMapping("/delete/request")
    public RsData<?> deleteRequest() {
        memberService.deleteRequest();
        return new RsData<>(
                MemberSuccessCode.DELETE_REQUEST.getCode(),
                MemberSuccessCode.DELETE_REQUEST.getMessage()
        );
    }

    @DeleteMapping
    public RsData<?> deleteImmediately(HttpServletResponse response) {
        memberService.deleteImmediately();

        cookieUtil.removeAccessTokenFromCookie(response);
        cookieUtil.removeRefreshTokenFromCookie(response);

        return new RsData<>(
                MemberSuccessCode.DELETE_REQUEST.getCode(),
                MemberSuccessCode.DELETE_REQUEST.getMessage()
        );
    }
}