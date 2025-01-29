package com.word_trainer.security.controllers;


import com.word_trainer.security.controllers.API.AuthAPI;
import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.security.domain.dto.TokensDto;
import com.word_trainer.security.domain.dto.ValidationResponseDto;
import com.word_trainer.security.services.CookieService;
import com.word_trainer.security.services.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

    private final AuthService service;
    private final CookieService cookieService;

    @Override
    public ResponseEntity<TokenResponseDto> login(LoginDto loginDto, HttpServletResponse response) {
        TokensDto dto = service.login(loginDto);
        cookieService.setRefreshTokenToCookie(response, dto.getRefreshToken());
        return ResponseEntity.ok(service.getTokenResponseDto(dto));
    }

    @Override
    public ResponseEntity<TokenResponseDto> refresh(HttpServletResponse response, String refreshToken) {
        TokensDto dto = service.refresh(refreshToken);
        cookieService.setRefreshTokenToCookie(response, dto.getRefreshToken());
        return ResponseEntity.ok(service.getTokenResponseDto(dto));
    }

    @Override
    public ResponseEntity<ValidationResponseDto> validation(String authorizationHeader) {
        return ResponseEntity.ok(service.validation(authorizationHeader));
    }

    @Override
    public void logout(HttpServletResponse response, String refreshToken) {
        service.logout(refreshToken);
        cookieService.removeRefreshTokenFromCookie(response);;
    }
}
