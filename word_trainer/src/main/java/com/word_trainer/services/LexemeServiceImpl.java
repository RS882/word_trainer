package com.word_trainer.services;

import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileFormatException;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileSizeException;
import com.word_trainer.exception_handler.server_exception.ServerIOException;
import com.word_trainer.repository.LexemeRepository;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.interfaces.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class LexemeServiceImpl implements LexemeService {

    private final LexemeRepository repository;

    private final TranslationService translationService;

    @Override
    public int getCountOfCreatedLexemeFromFile(LexemesFileDto dto) {
        checkFile(dto.getFile());
        return getCountOfCreatedLexemesFromExcelRowInFile(dto);
    }

    @Override
    public void createLexeme(LexemeDto dto) {
        createLexemeByLexemeDto(dto);
    }

    private void checkFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadFileSizeException();
        }
        if (!isExcelFile(file)) {
            throw new BadFileFormatException(file.getOriginalFilename());
        }
    }

    private boolean isExcelFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            WorkbookFactory.create(inputStream);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getCountOfCreatedLexemesFromExcelRowInFile(LexemesFileDto dto) {
        MultipartFile file = dto.getFile();
        Language sourceLanguage = dto.getSourceLanguage();
        Language targetLanguage = dto.getTargetLanguage();

        int count = 0;
        try (InputStream inputStream = file.getInputStream()) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                count += (isLexemeCreatedFromExcelRow(row, sourceLanguage, targetLanguage) ? 1 : 0);
            }
        } catch (IOException e) {
            throw new ServerIOException(e.getMessage());
        }
        return count;
    }

    private boolean isLexemeCreatedFromExcelRow(Row row, Language sourceLanguage, Language targetLanguage) {
        Cell sourceMeaningCell = row.getCell(0);
        Cell targetMeaningCell = row.getCell(1);
        Cell lexemeTypeCell = row.getCell(2);
        try {
            if (sourceMeaningCell.getCellType() == CellType.STRING &&
                    targetMeaningCell.getCellType() == CellType.STRING) {
                LexemeDto dto = LexemeDto.builder()
                        .sourceLanguage(sourceLanguage)
                        .targetLanguage(targetLanguage)
                        .sourceMeaning(sourceMeaningCell.getStringCellValue())
                        .targetMeaning(targetMeaningCell.getStringCellValue())
                        .type(getLexemeTypeFromExcelCell(lexemeTypeCell))
                        .build();
                createLexemeByLexemeDto(dto);
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            throw new ServerIOException(e.getMessage());
        }
    }

    private LexemeType getLexemeTypeFromExcelCell(Cell typeCell) {
        if (typeCell == null) {
            return LexemeType.WORD;
        }
        String value = typeCell.getStringCellValue();
        try {
            if (value.startsWith("фраза") || LexemeType.valueOf(value.toUpperCase()).equals(LexemeType.PHRASE)) {
                return LexemeType.PHRASE;
            } else {
                return LexemeType.WORD;
            }
        } catch (Exception e) {
            return LexemeType.WORD;
        }
    }

    private void createLexemeByLexemeDto(LexemeDto dto) {
        Translation existingTranslation = translationService
                .getTranslationByMeaning(dto.getSourceMeaning(), dto.getSourceLanguage());
        if (existingTranslation == null) {
            createNewLexeme(dto);
        } else {
            updateLexeme(existingTranslation, dto);
        }
    }

    @Transactional
    private void createNewLexeme(LexemeDto dto) {
        Lexeme newLexeme = Lexeme.builder()
                .type(dto.getType())
                .isActive(true)
                .translations(new HashSet<>())
                .build();
        Lexeme savedLexeme = repository.save(newLexeme);
        Translation sourceTranslation = Translation.builder()
                .isActive(true)
                .meaning(dto.getSourceMeaning())
                .language(dto.getSourceLanguage())
                .lexeme(savedLexeme)
                .build();
        Translation targetTranslation = Translation.builder()
                .isActive(true)
                .meaning(dto.getTargetMeaning())
                .language(dto.getTargetLanguage())
                .lexeme(savedLexeme)
                .build();
        savedLexeme.getTranslations().add(sourceTranslation);
        savedLexeme.getTranslations().add(targetTranslation);
        repository.save(savedLexeme);
    }

    private void updateLexeme(Translation existingTranslation, LexemeDto dto) {
        Lexeme currentLexeme = existingTranslation.getLexeme();
        translationService.updateTargetTranslation(dto, currentLexeme);
    }
}
