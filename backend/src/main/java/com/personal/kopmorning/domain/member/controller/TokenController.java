package com.personal.kopmorning.domain.member.controller;

import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.domain.member.responseCode.MemberSuccessCode;
import com.personal.kopmorning.global.entity.RsData;
import com.personal.kopmorning.global.exception.security.TokenException;
import com.personal.kopmorning.global.jwt.TokenDto;
import com.personal.kopmorning.global.jwt.TokenService;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping
    public RsData<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_MISSING.getCode(),
                    MemberErrorCode.TOKEN_MISSING.getMessage()
            );
        }

        TokenDto tokenDto = tokenService.reissueRefreshToken(refreshToken);

        cookieUtil.addAccessCookie(tokenDto.getAccessToken(), response);

        return new RsData<>(
                MemberSuccessCode.TOKEN_REISSUE.getCode(),
                MemberSuccessCode.TOKEN_REISSUE.getMessage(),
                tokenDto
        );
    }
}
