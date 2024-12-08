package com.word_trainer.security.services.mapping;

import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.security.domain.dto.TokensDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-08T12:15:21+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class TokenDtoMapperServiceImpl extends TokenDtoMapperService {

    @Override
    public TokenResponseDto toResponseDto(TokensDto tokensDto) {
        if ( tokensDto == null ) {
            return null;
        }

        TokenResponseDto.TokenResponseDtoBuilder tokenResponseDto = TokenResponseDto.builder();

        tokenResponseDto.userId( tokensDto.getUserId() );
        tokenResponseDto.accessToken( tokensDto.getAccessToken() );

        return tokenResponseDto.build();
    }
}
