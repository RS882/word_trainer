package com.word_trainer.repository;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.entity.UserLexemeResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("SELECT ulr FROM UserLexemeResult ulr WHERE ulr.user.id = :userId")
    List<UserLexemeResult> findAllByUserId(@Param("userId") Long userId);


    @Query("SELECT ulr FROM UserLexemeResult ulr WHERE ulr.user.id = :userId " +
            "AND ulr.sourceLanguage =:sourceLanguage " +
            "AND ulr.targetLanguage =:targetLanguage")
    Page<UserLexemeResult> findByUserIdAndLanguages(
            @Param("userId") Long userId,
            @Param("sourceLanguage") Language sourceLanguage,
            @Param("targetLanguage") Language targetLanguage,
            Pageable pageable);

    @Query("SELECT ulr FROM UserLexemeResult ulr WHERE ulr.user.id = :userId " +
            "AND ulr.lexeme.id IN  :lexemesIds")
    List<UserLexemeResult> findByUserIdAndLexemesIdS(@Param("userId") Long userId, @Param("lexemesIds") List<UUID> lexemesIds);
}
