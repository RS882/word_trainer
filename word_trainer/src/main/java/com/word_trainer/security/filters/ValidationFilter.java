package com.word_trainer.security.filters;


import com.word_trainer.security.domain.AuthInfo;
import com.word_trainer.security.services.TokenService;
import com.word_trainer.security.services.interfaces.AuthInfoService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ValidationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final AuthInfoService authInfoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authorizationHeader.substring(7);

        if (tokenService.validateAccessToken(accessToken)) {
            Claims claims = tokenService.getAccessTokenClaims(accessToken);
            AuthInfo authInfo = authInfoService.mapClaims(claims);
            if(authInfo != null){
                authInfo.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authInfo);
            }
        }
        filterChain.doFilter(request, response);
    }
}
