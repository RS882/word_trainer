package com.word_trainer.services;

import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileFormatException;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileSizeException;
import com.word_trainer.exception_handler.server_exception.ServerIOException;
import com.word_trainer.services.interfaces.LexemeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class LexemeServiceImpl implements LexemeService {
    @Override
    public int getCountOfCreatedLexemeFromFile(LexemesFileDto dto) {
        MultipartFile file = dto.getFile();
        checkFile(file);


        return getCountOfCreatedLexemesFromExcelRowInFile(dto);
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
            inputStream.mark(Integer.MAX_VALUE);
            WorkbookFactory.create(inputStream);
            inputStream.reset();
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
            inputStream.reset();

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

                return isLexemeCreatedByLexemeDto(dto);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
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

    private boolean isLexemeCreatedByLexemeDto(LexemeDto dto) {

        return true;
    }
}
