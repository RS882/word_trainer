package com.word_trainer.controllers;

import com.word_trainer.controllers.API.LexemeAPI;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.services.interfaces.LexemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LexemeController implements LexemeAPI {

    private final LexemeService lexemeService;

    @Override
    public ResponseEntity<ResponseMessageDto> createLexemesFromFile(LexemesFileDto dto) {
        int countOfCreatedLexemes = lexemeService.getCountOfCreatedLexemeFromFile(dto);
        ResponseMessageDto messageDto = new ResponseMessageDto(String.format("%d lexeme(s) created successfully", countOfCreatedLexemes));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageDto);
    }

    @Override
    public ResponseEntity<ResponseMessageDto> createLexeme(LexemeDto dto) {
        lexemeService.createLexeme(dto);
        ResponseMessageDto messageDto = new ResponseMessageDto( "Lexeme created successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageDto);
    }
}
