package com.word_trainer.domain.dto.lexeme;

import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Dto with a lexeme parameter")
public class LexemeDto {

    @Schema(description = "Source meaning for lexeme", example = "go")
    @NotNull(message = "Source meaning cannot be null")
    @NotBlank(message = "Source meaning cannot be blank")
    private String sourceMeaning;

    @Schema(description = "Target meaning for lexeme", example ="gehen")
    @NotNull(message = "Target meaning cannot be null")
    @NotBlank(message = "Target meaning cannot be blank")
    private String targetMeaning;

    @Schema(description = "Source language for lexeme", example ="EN" )
    @NotNull(message = "Source language cannot be null")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexeme", example ="DE")
    @NotNull(message = "Target language cannot be null")
    private Language targetLanguage;

    @Schema(description = "Type of lexeme", example ="WORD" )
    private LexemeType type;
}
