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

import java.util.List;


@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository repository;

    @Override
    public List<Translation> getTranslationsByMeaning(String meaning, Language language) {
        return repository.findByMeaningAndLanguage(meaning, language);
    }

    @Override
    @Transactional
    public void updateTargetTranslation(LexemeDto dto, Lexeme lexeme) {
        List<Translation> targetTranslations = repository.findByLexemeAndLanguage(lexeme, dto.getTargetLanguage());
        if (targetTranslations.isEmpty()) {
            Translation newTargetTranslation = Translation.builder()
                    .meaning(dto.getTargetMeaning())
                    .language(dto.getTargetLanguage())
                    .lexeme(lexeme)
                    .build();
            repository.save(newTargetTranslation);
        } else {
            String dtoTargetMeaning = dto.getTargetMeaning();
            targetTranslations.forEach(t -> {
                String currentMeaning = t.getMeaning();
                if (!currentMeaning.equals(dtoTargetMeaning)) {
                    t.setMeaning(currentMeaning + ", " + dtoTargetMeaning);
                }
            });
        }
    }
}
