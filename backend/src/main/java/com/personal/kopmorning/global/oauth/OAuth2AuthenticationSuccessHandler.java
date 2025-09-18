package com.personal.kopmorning.global.oauth;

import com.personal.kopmorning.global.jwt.TokenDto;
import com.personal.kopmorning.global.jwt.TokenService;
import com.personal.kopmorning.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;

    @Value("${redirect.url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        TokenDto tokenDto = tokenService.generateToken(authentication);

        cookieUtil.addAccessCookie(tokenDto.getAccessToken(), response);
        cookieUtil.addRefreshCookie(tokenDto.getRefreshToken(), response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.sendRedirect(redirectUrl);
    }
}