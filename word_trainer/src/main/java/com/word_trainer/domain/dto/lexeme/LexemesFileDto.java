package com.word_trainer.domain.dto.lexeme;

import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Schema(description = "Dto with a file with lexemes")
public class LexemesFileDto {

    @Schema(description = "File for creating lexemes")
    @NotNull(message = "File cannot be null")
    private MultipartFile file;

    @Schema(description = "Source language for lexemes")
    @NotNull(message = "Source language cannot be null")
    private Language sourceLanguage;

    @Schema(description = "Target language for lexemes")
    @NotNull(message = "Target language be null")
    private Language targetLanguage;
}
