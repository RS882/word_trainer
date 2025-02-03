package com.word_trainer.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Dto with a user results statistic data")
public class ResponseUserResultsDto {

    @Schema(description = "Source language for lexeme", example = "EN")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexeme", example = "DE")
    private Language targetLanguage;

    @Schema(description = "Total count of studied lexicons", example = "34")
    private int countOfResult;

    @Schema(description = "Total count of attempts", example = "78")
    private int countOfAttempts;

    @Schema(description = "Total count of successful attempts", example = "57")
    private int countOfSuccessfulAttempts;

    @JsonCreator
    public ResponseUserResultsDto(
            @JsonProperty("sourceLanguage") Language sourceLanguage,
            @JsonProperty("targetLanguage") Language targetLanguage,
            @JsonProperty("countOfResult") int countOfResult,
            @JsonProperty("countOfAttempts") int countOfAttempts,
            @JsonProperty("countOfSuccessfulAttempts") int countOfSuccessfulAttempts
    ) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.countOfResult = countOfResult;
        this.countOfAttempts = countOfAttempts;
        this.countOfSuccessfulAttempts = countOfSuccessfulAttempts;
    }
}
