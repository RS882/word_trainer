package com.word_trainer.security.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${expires.refresh}")
    private static int expiresRefreshInMinutes;

    public static final String COOKIE_REFRESH_TOKEN_NAME = "Refresh-token";

    public void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = makeCookie(COOKIE_REFRESH_TOKEN_NAME, refreshToken);
        response.addCookie(cookie);
    }

    public static Cookie makeCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(expiresRefreshInMinutes * 60);
        return cookie;
    }
}
