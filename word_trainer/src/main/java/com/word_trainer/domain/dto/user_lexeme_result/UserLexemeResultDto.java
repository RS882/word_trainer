package com.word_trainer.domain.dto.user_lexeme_result;

import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto with a user lexemes results")
public class UserLexemeResultDto {

    @Schema(description = "Source language for lexeme", example = "EN")
    @NotNull(message = "Source language cannot be null")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexeme", example = "DE")
    @NotNull(message = "Target language cannot be null")
    private Language targetLanguage;

    @Schema(
            description = "Set of user lexemes results",
            example = """
                    [
                        {
                            "lexemeId": "a52395dc-04cc-44bd-8bc6-f87c46165688",
                            "attempts": 4,
                            "successfulAttempts": 2
                        },
                        {
                            "lexemeId": "b6a0f9f3-b60f-4d5a-a84c-f57bfa90e24f",
                            "attempts": 5,
                            "successfulAttempts": 4
                        },
                        {
                            "lexemeId": "c6b7f2e3-c84f-5d5c-b94d-f67cfa91e35f",
                            "attempts": 3,
                            "successfulAttempts": 3
                        }
                    ]
                    """
    )
    @NotNull(message = "Set of user lexemes results cannot be null")
    @ArraySchema(schema = @Schema(implementation = UserResultsDto.class))
    private Set<@Valid UserResultsDto> resultDtos;
}
