package com.word_trainer.domain.dto.lexeme;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.entity.Lexeme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LexemeTranslationDto {

    private Language sourceLanguage;

    private Language targetLanguage;

    private Lexeme lexeme;
}
