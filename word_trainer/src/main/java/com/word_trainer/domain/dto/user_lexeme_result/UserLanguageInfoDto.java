package com.word_trainer.domain.dto.user_lexeme_result;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLanguageInfoDto {

    private User user;

    private Language sourceLanguage;

    private Language targetLanguage;

}
