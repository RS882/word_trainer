package com.word_trainer.domain.dto.user_lexeme_result;

import com.word_trainer.domain.dto.user_lexeme_result.validators.ValidUserResults;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Dto with a user result")
@ValidUserResults
public class UserResultsDto {

    @Schema(description = "Lexeme ID", example = "a52395dc-04cc-44bd-8bc6-f87c46165688")
    @NotNull(message = "Id cannot be null")
    private UUID lexemeId;

    @Schema(description = "Count of attempts", example = "4")
    @Min(value = 1, message = "Attempts cannot be greater 0")
    private int attempts;

    @Schema(description = "Count of attempts", example = "2")
    @Min(value = 0, message = "Successful attempts cannot be greater or equals 0")
    private int successfulAttempts;
}
