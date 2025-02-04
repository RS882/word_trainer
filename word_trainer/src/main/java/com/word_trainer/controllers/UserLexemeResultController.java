package com.word_trainer.controllers;

import com.word_trainer.constants.language.Language;
import com.word_trainer.controllers.API.UserLexemeResultAPI;
import com.word_trainer.domain.dto.response.PageResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.ResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.services.interfaces.UserLexemeResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.word_trainer.services.utilities.PaginationUtilities.getPageable;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserLexemeResultController implements UserLexemeResultAPI {

    private final UserLexemeResultService service;

    @Override
    public ResponseEntity<ResponseMessageDto> saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser) {
        service.saveOrUpdateUserLexemeResults(dto, currentUser);
        String messageText = "User results upsert successfully";
        log.info(messageText);
        ResponseMessageDto messageDto = new ResponseMessageDto(messageText);
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageDto);
    }

    @Override
    public ResponseEntity<List<ResponseUserResultsDto>> getUserStudyStatistics(User currentUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getStudyStatisticsByUserId(currentUser.getId()));
    }

    @Override
    public ResponseEntity<PageResponseUserResultsTranslationDto> getTranslations(
            User currentUser,
            String sourceLanguage,
            String targetLanguage,
            int page,
            int size,
            String sortBy,
            Boolean isAsc) {
        Page<ResponseUserResultsTranslationDto> translations = service.getUserTranslationResultsWithPagination(
                currentUser.getId(),
                Language.valueOf(sourceLanguage),
                Language.valueOf(targetLanguage),
                getPageable(page, size, sortBy, isAsc)
        );
        return ResponseEntity.status((HttpStatus.OK))
                .body(new PageResponseUserResultsTranslationDto(translations));
    }
}

