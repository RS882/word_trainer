package com.word_trainer.security.services.interfaces;


import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.security.domain.dto.TokensDto;
import com.word_trainer.security.domain.dto.ValidationResponseDto;

public interface AuthService {
    TokensDto login(LoginDto loginDto);

    TokensDto refresh(String refreshToken);

    ValidationResponseDto validation(String authorizationHeader);

    void logout(String refreshToken);

    TokenResponseDto getTokenResponseDto(TokensDto tokensDto);

}
