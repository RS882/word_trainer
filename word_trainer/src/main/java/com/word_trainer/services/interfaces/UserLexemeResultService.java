package com.word_trainer.services.interfaces;

import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;

import java.util.List;

public interface UserLexemeResultService {

    void saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser);

    List<ResponseUserResultsDto> getStudyStatisticsByUserId(Long userId);
}
