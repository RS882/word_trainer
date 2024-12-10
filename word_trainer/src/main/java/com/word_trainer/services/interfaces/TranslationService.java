package com.word_trainer.services.interfaces;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;

public interface TranslationService {

    Translation getTranslationByMeaning(String meaning, Language language);

    void createPairOfTranslation(LexemeDto dto, Lexeme lexeme);

    void updateTargetTranslation(LexemeDto dto, Lexeme lexeme);
}
