package com.word_trainer.security.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${expires.refresh}")
    private int expiresRefreshInMinutes;

    public static final String COOKIE_REFRESH_TOKEN_NAME = "Refresh-token";

    public void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = makeCookie(COOKIE_REFRESH_TOKEN_NAME, refreshToken);
        response.addCookie(cookie);
    }

    public void removeRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = makeCookie(COOKIE_REFRESH_TOKEN_NAME);
        response.addCookie(cookie);
    }

    public Cookie makeCookie(String name, String value) {
        int maxAge = expiresRefreshInMinutes * 60;
        return makeCookie(name, value, maxAge);
    }

    private Cookie makeCookie(String name) {
        return makeCookie(name, "", 0);
    }

    private Cookie makeCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //TODO for https - make secure true
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
