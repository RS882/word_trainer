package com.word_trainer.services;

import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;
import com.word_trainer.repository.UserLexemeResultRepository;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.interfaces.UserLexemeResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLexemeResultServiceImpl implements UserLexemeResultService {

    private final UserLexemeResultRepository repository;

    private final LexemeService lexemeService;

    @Override
    @Transactional
    public void saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser) {

        Set<UserLexemeResult> newResultsOfUser = new HashSet<>();

        UserLanguageInfoDto userLanguageInfoDto = UserLanguageInfoDto.builder()
                .user(currentUser)
                .sourceLanguage(dto.getSourceLanguage())
                .targetLanguage(dto.getTargetLanguage())
                .build();

        dto.getResultDtos().forEach(r -> {

            UserLexemeResult result = getUserLexemeResultByParams(userLanguageInfoDto, r.getLexemeId());

            if (result != null) {
                result.setAttempts(result.getAttempts() + r.getAttempts());
                result.setSuccessfulAttempts(result.getSuccessfulAttempts() + r.getSuccessfulAttempts());
            } else {
                UserLexemeResult newResult = createNewUserLexemeResult(r, userLanguageInfoDto);
                newResultsOfUser.add(newResult);
            }
        });

        if (!newResultsOfUser.isEmpty()) {
            repository.saveAll(newResultsOfUser);
        }
    }

    private UserLexemeResult getUserLexemeResultByParams(UserLanguageInfoDto dto, UUID lexemeId) {
        return repository.findByUserAndLanguagesAndLexemeId(dto, lexemeId)
                .orElse(null);
    }

    private UserLexemeResult createNewUserLexemeResult(UserResultsDto dto, UserLanguageInfoDto infoDto) {
        Lexeme lexeme = lexemeService.getLexemesById(dto.getLexemeId());
        return UserLexemeResult.builder()
                .attempts(dto.getAttempts())
                .successfulAttempts(dto.getSuccessfulAttempts())
                .sourceLanguage(infoDto.getSourceLanguage())
                .targetLanguage(infoDto.getTargetLanguage())
                .user(infoDto.getUser())
                .lexeme(lexeme)
                .build();
    }
}
