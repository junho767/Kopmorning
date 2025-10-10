package com.personal.kopmorning.global.jwt;

import com.personal.kopmorning.global.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String accessToken = CookieUtil.getAccessTokenFromCookie(httpRequest);

        try {
            if (accessToken != null) {
                tokenService.validateToken(accessToken);
                Authentication authentication = tokenService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // Access token 만료 시 refresh token으로 자동 재발급 시도
            log.debug("액세스 토큰 만료, 토큰 재발급 시도: {}", e.getMessage());
            tryRefreshToken(httpRequest, httpResponse);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // 무효 토큰: 인증 컨텍스트를 비우고 계속 진행
            SecurityContextHolder.clearContext();
            log.debug("유효하지 않은 토큰: {}", e.getMessage());
        }

        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.error("필터 체인 오류: {}", e.getMessage());
        }
    }

    private void tryRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = CookieUtil.getRefreshTokenFromCookie(request);
            
            if (refreshToken != null) {
                // Refresh token 로 새로운 access token 발급
                TokenDto tokenDto = tokenService.reissueRefreshToken(refreshToken);
                
                // 새로운 access token을 쿠키에 설정
                cookieUtil.addAccessCookie(tokenDto.getAccessToken(), response);
                
                // 새로운 access token 로 인증 정보 설정
                Authentication authentication = tokenService.getAuthentication(tokenDto.getAccessToken());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("토큰 재발급 성공");
            } else {
                // Refresh token이 없으면 컨텍스트 비우기
                SecurityContextHolder.clearContext();
                log.debug("리프레시 토큰이 존재하지 않습니다. 재로그인 부탁드립니다.");
            }
        } catch (Exception e) {
            // Refresh token 만료되었거나 유효하지 않은 경우
            SecurityContextHolder.clearContext();
            log.debug("토큰 재발급 실패: {}", e.getMessage());
        }
    }
}

