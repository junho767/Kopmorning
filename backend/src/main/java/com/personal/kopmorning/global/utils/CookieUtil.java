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

    public void addAccessCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        cookie.setMaxAge(accessTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        response.addCookie(cookie);
    }

    public void addRefreshCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        response.addCookie(cookie);
    }

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
/**
 * todo 배포 시 사용할 refreshToken 쿠키 추가 방법
    public void addRefreshCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setAttribute(SAME_SITE, NONE);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
 * todo 배포 시 사용할 accessToken 쿠키 추가 방법
    public void addAccessCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        cookie.setMaxAge(accessTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setAttribute(SAME_SITE, NONE);
        cookie.setHttpOnly(true); // HTTPS 에만 전달 가능하게 함.
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
**/
}
