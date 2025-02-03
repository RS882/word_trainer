package com.word_trainer.services.mapping;

import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.entity.UserLexemeResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
}
