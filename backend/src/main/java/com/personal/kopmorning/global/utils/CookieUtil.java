package com.personal.kopmorning.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String SAME_SITE = "sameSite";
    private static final String NONE = "none";

    @Value("${jwt.expiration.access-token}")
    private int accessTokenExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private int refreshTokenExpiration;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;

    @Value("${jwt.cookie-path}")
    private String cookiePath;

    public void removeAccessTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public void removeRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public static String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void addRefreshCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setSecure(true);                  // HTTPS 전용
        cookie.setHttpOnly(true);                // JS에서 접근 불가, XSS 방어
        cookie.setAttribute("SameSite", "None"); // 다른 도메인에서도 전송 가능 (CORS 대응)
        response.addCookie(cookie);
    }

    public void addAccessCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        cookie.setMaxAge(accessTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }
}
