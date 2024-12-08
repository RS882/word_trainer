package com.word_trainer.security.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensDto {
    private Long userId;
    private String refreshToken;
    private String accessToken;
}
