package com.word_trainer.domain.dto.user_lexeme_result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto fro change status user lexemes results")
public class UpdateStatusUserLexemeResultDto {

    @Schema(description = "Lexeme ID", example = "a52395dc-04cc-44bd-8bc6-f87c46165688")
    @NotNull(message = "Id cannot be null")
    private UUID lexemeId;

    @Schema(description = "Is lexeme active for user", example = "true")
    @NotNull(message = "Active cannot be null")
    private Boolean isActive;
}
