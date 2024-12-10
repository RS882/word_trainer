package com.word_trainer.services.interfaces;

import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.entity.User;
import org.springframework.stereotype.Service;


public interface LexemeService {

    int getCountOfCreatedLexemeFromFile(LexemesFileDto dto);
}
