package com.word_trainer.services;

import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLanguageInfoDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;
import com.word_trainer.repository.UserLexemeResultRepository;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.interfaces.UserLexemeResultService;
import com.word_trainer.services.mapping.UserLexemeResultMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserLexemeResultServiceImpl implements UserLexemeResultService {

    private final UserLexemeResultRepository repository;

    private final LexemeService lexemeService;

    private final UserLexemeResultMapperService userLexemeResultMapperService;

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
                userLexemeResultMapperService.toUpdatedUserLexemeResult(result, r);
            } else {
                UserLexemeResult newResult = buildNewUserLexemeResult(r, userLanguageInfoDto);
                newResultsOfUser.add(newResult);
            }
        });

        if (!newResultsOfUser.isEmpty()) {
            repository.saveAll(newResultsOfUser);
        }
    }

    @Override
    public List<ResponseUserResultsDto> getStudyStatisticsByUserId(Long userId) {

        List<UserLexemeResult> userResults = repository.findAllByUserId(userId);

        if (userResults.isEmpty()) return List.of();

        List<ResponseUserResultsDto> response = new ArrayList<>();

        for (UserLexemeResult result : userResults) {

            ResponseUserResultsDto resultOfUser = response.stream()
                    .filter(dto -> dto.getSourceLanguage() == result.getSourceLanguage()
                            && dto.getTargetLanguage() == result.getTargetLanguage())
                    .findFirst()
                    .orElse(null);

            if (resultOfUser != null) {
                userLexemeResultMapperService.toUpdatedResponseUserResultsDto(result, resultOfUser);
            } else {
                response.add(userLexemeResultMapperService.toResponseUserResultsDto(result));
            }
        }
        return response;
    }

    private UserLexemeResult getUserLexemeResultByParams(UserLanguageInfoDto dto, UUID lexemeId) {
        return repository.findByUserAndLanguagesAndLexemeId(dto, lexemeId)
                .orElse(null);
    }

    private UserLexemeResult buildNewUserLexemeResult(UserResultsDto dto, UserLanguageInfoDto infoDto) {
        Lexeme lexeme = lexemeService.getLexemesById(dto.getLexemeId());
        UserLexemeResult result = userLexemeResultMapperService.toNewUserLexemeResult(dto, infoDto);
        result.setLexeme(lexeme);
        return result;
    }
}
