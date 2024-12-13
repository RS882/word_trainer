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

    @Schema(description = "Current page number", example = "6")
    private int pageNumber;

    @Schema(description = "Current page size", example = "20")
    private int pageSize;

    @Schema(description = "Total number of pages", example = "134")
    public int totalPages;

    @Schema(description = "Total number of elements", example = "345")
    private long totalElements;

    @Schema(description = "Is first page?", example = "true")
    private Boolean isFirstPage;

    @Schema(description = "Is last page?", example = "true")
    private Boolean isLastPage;
}
