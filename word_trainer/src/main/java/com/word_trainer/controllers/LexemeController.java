package com.word_trainer.controllers;

import com.word_trainer.constants.language.Language;
import com.word_trainer.controllers.API.LexemeAPI;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.dto.response.ResponseLexemesDto;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.services.interfaces.LexemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LexemeController implements LexemeAPI {

    private final LexemeService lexemeService;

    @Override
    public ResponseEntity<ResponseMessageDto> createLexemesFromFile(LexemesFileDto dto) {
        int countOfCreatedLexemes = lexemeService.getCountOfCreatedLexemeFromFile(dto);
        String messageText = String.format("%d lexeme(s) created successfully", countOfCreatedLexemes);
        log.info(messageText);
        ResponseMessageDto messageDto = new ResponseMessageDto(messageText);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageDto);
    }

    @Override
    public ResponseEntity<ResponseMessageDto> createLexeme(LexemeDto dto) {
        lexemeService.createLexeme(dto);
        String messageText = "Lexeme created successfully";
        log.info(messageText);
        ResponseMessageDto messageDto = new ResponseMessageDto(messageText);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageDto);
    }

    @Override
    public ResponseEntity<ResponseLexemesDto> getLexemes(int count,
                                                         Language sourceLanguage,
                                                         Language targetLanguage,
                                                         User currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(
                lexemeService.getLexemes(count, sourceLanguage, targetLanguage, currentUser)
        );
    }
}
