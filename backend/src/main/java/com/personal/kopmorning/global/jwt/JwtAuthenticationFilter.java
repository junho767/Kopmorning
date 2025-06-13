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

            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_EXPIRE.getCode(),
                    MemberErrorCode.TOKEN_EXPIRE.getMessage()
            );
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | IOException |
                 ServletException e) {
            throw new TokenException(
                    MemberErrorCode.TOKEN_INVALID.getCode(),
                    MemberErrorCode.TOKEN_INVALID.getMessage()
            );
        }
    }
}

