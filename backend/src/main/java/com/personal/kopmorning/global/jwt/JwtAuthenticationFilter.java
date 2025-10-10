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
        String accessToken = cookieUtil.getAccessTokenFromCookie(httpRequest);

        try {
            if (accessToken != null) {
                tokenService.validateToken(accessToken);

                if (tokenService.isAccessTokenBlacklisted(accessToken)) {
                    SecurityContextHolder.clearContext();
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Authentication authentication = tokenService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            log.debug("액세스 토큰 만료, 토큰 재발급 시도: {}", e.getMessage());
            tryRefreshToken(httpRequest, httpResponse);
            return;
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
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
            String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (tokenService.isRefreshTokenBlacklisted(refreshToken)) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            TokenDto tokenDto = tokenService.reissueRefreshToken(refreshToken);
            cookieUtil.addAccessCookie(tokenDto.getAccessToken(), response);
            Authentication authentication = tokenService.getAuthentication(tokenDto.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
