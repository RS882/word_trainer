package com.word_trainer.services.interfaces;

import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;


public interface LexemeService {

    int getCountOfCreatedLexemeFromFile(LexemesFileDto dto);

    void createLexeme(LexemeDto dto);
}
