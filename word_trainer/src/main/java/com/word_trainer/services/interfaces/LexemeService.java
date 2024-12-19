package com.word_trainer.services.interfaces;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.dto.response.ResponseLexemesDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.User;

import java.util.UUID;


public interface LexemeService {

    int getCountOfCreatedLexemeFromFile(LexemesFileDto dto);

    void createLexeme(LexemeDto dto);

    ResponseLexemesDto getLexemes(int count, Language sourceLanguage,
                                  Language targetLanguage, User currectUser);

    Lexeme getLexemesById(UUID lexemeId);

    Lexeme createNewLexeme(LexemeDto dto);
}
