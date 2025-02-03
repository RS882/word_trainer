package com.word_trainer.domain.dto.user_lexeme_result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.response.ResponseTranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@Schema(description = "Dto with a user results statistic data")
public class UserResultsTranslationDto extends ResponseTranslationDto {

    @Schema(description = "Is lexeme active for user", example = "true")
    private Boolean isActive;

    @Schema(description = "Count of attempts", example = "4")
    @Min(value = 1, message = "Attempts cannot be greater 0")
    private int attempts;

    @Schema(description = "Count of attempts", example = "2")
    @Min(value = 0, message = "Successful attempts cannot be greater or equals 0")
    private int successfulAttempts;

    @JsonCreator
    public UserResultsTranslationDto(
            @JsonProperty("lexemeId") UUID lexemeId,
            @JsonProperty("type") LexemeType type,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("attempts") int attempts,
            @JsonProperty("successfulAttempts") int successfulAttempts,
            @JsonProperty("translations") Map<Language, Map<UUID, String>> translations
    ) {
        super(lexemeId, type, translations);
        this.isActive = isActive;
        this.attempts = attempts;
        this.successfulAttempts = successfulAttempts;
    }
}
