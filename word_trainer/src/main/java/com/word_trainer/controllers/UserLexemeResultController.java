package com.word_trainer.controllers;

import com.word_trainer.controllers.API.UserLexemeResultAPI;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.services.interfaces.UserLexemeResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserLexemeResultController implements UserLexemeResultAPI {

    private final UserLexemeResultService service;

    @Override
    public ResponseEntity<ResponseMessageDto> saveOrUpdateUserLexemeResults(UserLexemeResultDto dto, User currentUser) {
        String messageText = "User results upsert successfully";
        log.info(messageText);
        ResponseMessageDto messageDto = new ResponseMessageDto(messageText);
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageDto);
    }
}

