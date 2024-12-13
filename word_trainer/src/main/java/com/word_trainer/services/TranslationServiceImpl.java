package com.word_trainer.services;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;
import com.word_trainer.repository.TranslationRepository;
import com.word_trainer.services.interfaces.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository repository;

    @Override
    public Translation getTranslationByMeaning(String meaning, Language language) {
        return repository.findByMeaningAndLanguage(meaning, language)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateTargetTranslation(LexemeDto dto, Lexeme lexeme) {
        Translation targetTranslation = repository.findByLexemeAndLanguage(lexeme, dto.getTargetLanguage())
                .orElse(null);
        if (targetTranslation == null) {
            Translation newTargetTranslation = Translation.builder()
                    .isActive(true)
                    .meaning(dto.getTargetMeaning())
                    .language(dto.getTargetLanguage())
                    .lexeme(lexeme)
                    .build();
            repository.save(newTargetTranslation);
        } else {
            String currentMeaning = targetTranslation.getMeaning();
            String dtoTargetMeaning = dto.getTargetMeaning();
            if (!currentMeaning.equals(dtoTargetMeaning)) {
                targetTranslation.setMeaning(currentMeaning + ", " + dtoTargetMeaning);
            }
        }
    }
}
