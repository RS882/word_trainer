package com.word_trainer.repository;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.entity.Lexeme;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface LexemeRepository extends JpaRepository<Lexeme, UUID> {

    @Query("SELECT l FROM Lexeme l " +
            "JOIN l.translations t " +
            "WHERE (t.language = :sourceLanguage OR t.language = :targetLanguage) " +
            "AND l.id NOT IN :excludedLexemeIds " +
            "GROUP BY l.id " +
            "HAVING COUNT(DISTINCT t.language) = 2 " +
            "ORDER BY RAND()")
    List<Lexeme> findRandomLexemes(Pageable pageable,
                                   @Param("sourceLanguage") Language sourceLanguage,
                                   @Param("targetLanguage") Language targetLanguage,
                                   @Param("excludedLexemeIds") List<UUID> excludedLexemeIds);
}
