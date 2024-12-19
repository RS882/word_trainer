package com.word_trainer.services.interfaces;

import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;

public interface UserLexemeResultService {

    void saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser);

    UserLexemeResult buildNewUserLexemeResult(UserResultsDto dto, UserLanguageInfoDto infoDto);
}
