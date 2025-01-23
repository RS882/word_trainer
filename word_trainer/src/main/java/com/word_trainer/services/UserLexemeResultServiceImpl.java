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
        UserLanguageInfoDto userLanguageInfoDto = UserLanguageInfoDto.builder()
                .user(currentUser)
                .sourceLanguage(dto.getSourceLanguage())
                .targetLanguage(dto.getTargetLanguage())
                .build();

        Set<UserLexemeResult> newResultsOfUser = new HashSet<>();

        dto.getResultDtos().forEach(r -> {

            UserLexemeResult result = getUserLexemeResultByParams(userLanguageInfoDto, r.getLexemeId());

            if (result != null) {
                updateAttemptsInUserLexemeResult(result, r);
            } else {
                UserLexemeResult newResult = buildNewUserLexemeResult(r, userLanguageInfoDto);
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


    private UserLexemeResult buildNewUserLexemeResult(UserResultsDto dto, UserLanguageInfoDto infoDto) {
        Lexeme lexeme = lexemeService.getLexemesById(dto.getLexemeId());
        return UserLexemeResult.builder()
                .attempts(dto.getAttempts())
                .successfulAttempts(dto.getSuccessfulAttempts())
                .sourceLanguage(infoDto.getSourceLanguage())
                .targetLanguage(infoDto.getTargetLanguage())
                .user(infoDto.getUser())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .lexeme(lexeme)
                .build();
    }

    private void updateAttemptsInUserLexemeResult(UserLexemeResult result,
                                                  UserResultsDto dto) {
        result.setAttempts(result.getAttempts() + dto.getAttempts());
        result.setSuccessfulAttempts(result.getSuccessfulAttempts() + dto.getSuccessfulAttempts());
        if (dto.getIsActive() != null) {
            result.setIsActive(dto.getIsActive());
        }
    }
}
