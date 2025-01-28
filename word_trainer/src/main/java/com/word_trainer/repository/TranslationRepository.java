package com.word_trainer.repository;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TranslationRepository extends JpaRepository<Translation, UUID> {

    List<Translation> findByMeaningAndLanguage(String meaning, Language language);

    List<Translation> findByLexemeAndLanguage(Lexeme lexeme, Language language);

    List<Translation> findByMeaningIn(List<String> meanings);
}
