package com.word_trainer.services.interfaces;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.ResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.user_lexeme_result.UpdateStatusUserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserLexemeResultService {

    void saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser);

    List<ResponseUserResultsDto> getStudyStatisticsByUserId(Long userId);

    Page<ResponseUserResultsTranslationDto> getUserTranslationResultsWithPagination(
            Long userId,
            Language sourceLanguage,
            Language targetLanguage,
            Pageable pageable
    );

    void updateStatusOfUserLexemesResults(Long userId, List<UpdateStatusUserLexemeResultDto> dto);
}
