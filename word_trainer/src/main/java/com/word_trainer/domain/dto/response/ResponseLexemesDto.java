package com.word_trainer.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Response DTO with lexemes")
public class ResponseLexemesDto {

    @Schema(description = "Source language for lexeme", example = "EN")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexeme", example = "DE")
    private Language targetLanguage;

    @Schema(description = "Response DTO with translations")
    @ArraySchema(schema = @Schema(implementation = ResponseTranslationDto.class))
    private List<ResponseTranslationDto> translations;

    @JsonCreator
    public ResponseLexemesDto(
            @JsonProperty("sourceLanguage") Language sourceLanguage,
            @JsonProperty("targetLanguage") Language targetLanguage,
            @JsonProperty("translations") List<ResponseTranslationDto> translations) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translations = translations;
    }
}
