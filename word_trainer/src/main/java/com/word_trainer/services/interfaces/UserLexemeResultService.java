package com.word_trainer.services.interfaces;

import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;

public interface UserLexemeResultService {

    void saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser);

}
