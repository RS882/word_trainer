package com.word_trainer.repository;

import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.entity.UserLexemeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserLexemeResultRepository extends JpaRepository<UserLexemeResult, UUID> {

    @Query("SELECT ulr FROM UserLexemeResult ulr WHERE ulr.user = :#{#dto.user} " +
            "AND ulr.sourceLanguage = :#{#dto.sourceLanguage} " +
            "AND ulr.targetLanguage = :#{#dto.targetLanguage} " +
            "AND ulr.lexeme.id = :lexemeId")
    Optional<UserLexemeResult> findByUserAndLanguagesAndLexemeId(
            @Param("dto") UserLanguageInfoDto dto,
            @Param("lexemeId") UUID lexemeId);
}
