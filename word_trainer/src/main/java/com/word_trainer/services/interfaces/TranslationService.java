package com.word_trainer.services.interfaces;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;

import java.util.List;

public interface TranslationService {

    List<Translation> getTranslationsByMeaning(String meaning, Language language);

    void updateTargetTranslation(LexemeDto dto, Lexeme lexeme);
}
