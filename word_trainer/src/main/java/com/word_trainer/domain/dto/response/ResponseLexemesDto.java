package com.word_trainer.domain.dto.response;

import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO with lexemes")
public class ResponseLexemesDto {

    @Schema(description = "Source language for lexeme", example = "EN")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexeme", example = "DE")
    private Language targetLanguage;

    @Schema(description = "Response DTO with translations")
    private List<ResponseTranslationDto> translations;
}
