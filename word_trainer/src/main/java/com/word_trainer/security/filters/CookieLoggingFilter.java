package com.word_trainer.security.filters;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class CookieLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] incomingCookies = request.getCookies();
        if (incomingCookies != null) {
            Arrays.stream(incomingCookies).forEach(cookie ->
                    System.out.println("Incoming Cookie: " + cookie.getName() + " = " + cookie.getValue())
            );
        } else {
            System.out.println("No Incoming Cookies");
        }

        filterChain.doFilter(request, response);

        String[] outgoingCookies = response.getHeaders("Set-Cookie").toArray(new String[0]);
        if (outgoingCookies.length > 0) {
            Arrays.stream(outgoingCookies).forEach(cookie ->
                    System.out.println("Outgoing Cookie: " + cookie)
            );
        } else {
            System.out.println("No Outgoing Cookies");
        }
    }
}

