package com.word_trainer.security.services.mapping;


import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.security.domain.dto.TokensDto;
import org.mapstruct.Mapper;

@Mapper
public abstract class TokenDtoMapperService {
   public abstract TokenResponseDto toResponseDto(TokensDto tokensDto) ;
}
