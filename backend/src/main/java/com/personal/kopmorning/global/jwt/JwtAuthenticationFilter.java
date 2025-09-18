package com.personal.kopmorning.global.jwt;

import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.security.TokenException;
import com.personal.kopmorning.global.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final TokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String accessToken = CookieUtil.getAccessTokenFromCookie(httpRequest);

        try {
            if (accessToken != null) {
                tokenService.validateToken(accessToken);
                Authentication authentication = tokenService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // 만료 토큰: 인증 컨텍스트를 비우고 계속 진행 (퍼블릭 엔드포인트 접근 허용)
            SecurityContextHolder.clearContext();
            log.debug("Access token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // 무효 토큰: 인증 컨텍스트를 비우고 계속 진행
            SecurityContextHolder.clearContext();
            log.debug("Invalid token: {}", e.getMessage());
        }

        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.error("Chain error: {}", e.getMessage());
        }
    }
}

