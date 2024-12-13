package com.word_trainer.domain.dto.response;

import com.word_trainer.constants.language.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO with translations")
public class ResponseTranslationDto {

    @Schema(description = "Lexeme ID", example = "a52395dc-04cc-44bd-8bc6-f87c46165688")
    private UUID lexemeId;

    @Schema(description = "Translations of lexeme", example = """
            {
                "EN": {"b6a0f9f3-b60f-4d5a-a84c-f57bfa90e24f": "go"},
            	"DE": {"3d9a9200-3485-44e1-8f6e-8d85375be595": "gehen"}
            }
            """)
    private Map<Language, Map<UUID, String>> translations;
}
