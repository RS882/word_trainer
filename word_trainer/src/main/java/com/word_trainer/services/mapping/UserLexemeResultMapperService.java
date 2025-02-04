package com.word_trainer.services.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.ResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import com.word_trainer.domain.entity.UserLexemeResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;
import java.util.UUID;

@Mapper
public abstract class UserLexemeResultMapperService {

    @Mapping(target = "countOfAttempts", source = "attempts")
    @Mapping(target = "countOfSuccessfulAttempts", source = "successfulAttempts")
    @Mapping(target = "countOfResult", expression = "java(1)")
    public abstract ResponseUserResultsDto toResponseUserResultsDto(UserLexemeResult result);

    @Mapping(target = "sourceLanguage", ignore = true)
    @Mapping(target = "targetLanguage", ignore = true)
    @Mapping(target = "countOfAttempts", expression = "java(dto.getCountOfAttempts() + result.getAttempts())")
    @Mapping(target = "countOfSuccessfulAttempts", expression = "java(dto.getCountOfSuccessfulAttempts() + result.getSuccessfulAttempts())")
    @Mapping(target = "countOfResult", expression = "java(dto.getCountOfResult() + 1)")
    public abstract ResponseUserResultsDto toUpdatedResponseUserResultsDto(
            UserLexemeResult result,
            @MappingTarget ResponseUserResultsDto dto);


    @Mapping(target = "lexeme", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "attempts", expression = "java(dto.getAttempts())")
    @Mapping(target = "successfulAttempts", expression = "java(dto.getSuccessfulAttempts())")
    @Mapping(target = "sourceLanguage", expression = "java(infoDto.getSourceLanguage())")
    @Mapping(target = "targetLanguage", expression = "java(infoDto.getTargetLanguage())")
    @Mapping(target = "user", expression = "java(infoDto.getUser())")
    @Mapping(target = "isActive", expression = "java(getIsActive(dto))")
    public abstract UserLexemeResult toNewUserLexemeResult(UserResultsDto dto, UserLanguageInfoDto infoDto);

    @Mapping(target = "lexeme", ignore = true)
    @Mapping(target = "sourceLanguage", ignore = true)
    @Mapping(target = "targetLanguage", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "attempts", expression = "java(result.getAttempts() + dto.getAttempts())")
    @Mapping(target = "successfulAttempts", expression = "java(result.getSuccessfulAttempts() + dto.getSuccessfulAttempts())")
    @Mapping(target = "isActive", expression = "java(updateIsActive(dto, result))")
    public abstract UserLexemeResult toUpdatedUserLexemeResult(
            @MappingTarget UserLexemeResult result,
            UserResultsDto dto);

    protected boolean getIsActive(UserResultsDto dto) {
        return dto.getIsActive() != null ? dto.getIsActive() : true;
    }

    protected boolean updateIsActive(UserResultsDto dto, UserLexemeResult result) {
        return (dto.getIsActive() != null) ? dto.getIsActive() : result.getIsActive();
    }
}
