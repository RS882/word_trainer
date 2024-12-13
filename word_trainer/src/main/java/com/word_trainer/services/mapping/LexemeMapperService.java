package com.word_trainer.services.mapping;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeTranslationDto;
import com.word_trainer.domain.dto.response.ResponseTranslationDto;
import com.word_trainer.domain.entity.Translation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mapper
public abstract class LexemeMapperService {

    @Mapping(target = "lexemeId", source = "lexeme.id")
    @Mapping(target = "type", source = "lexeme.type")
    @Mapping(target = "translations", expression = "java(toTranslationsMap(lexemeDto))")
    public abstract ResponseTranslationDto toResponseTranslationDto(LexemeTranslationDto lexemeDto);

    protected Map<Language, Map<UUID, String>> toTranslationsMap(LexemeTranslationDto lexemeDto) {

        Set<Translation> translations = lexemeDto.getLexeme().getTranslations();
        Map<Language, Map<UUID, String>> translationsMap = new HashMap<>();

        translations.forEach(t -> {
            Language language = t.getLanguage();
            if (language == lexemeDto.getSourceLanguage() || language == lexemeDto.getTargetLanguage()) {
                Map<UUID, String> languageMap = translationsMap.computeIfAbsent(language, k -> new HashMap<>());
                languageMap.put(t.getId(), t.getMeaning());
            }
        });
        return translationsMap.size() == 2 ? translationsMap : null;
    }
}
