package com.word_trainer.services;

import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemeTranslationDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.dto.response.ResponseLexemesDto;
import com.word_trainer.domain.dto.response.ResponseTranslationDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileFormatException;
import com.word_trainer.exception_handler.bad_requeat.exceptions.BadFileSizeException;
import com.word_trainer.exception_handler.not_found.exceptions.LexemeNotFoundException;
import com.word_trainer.exception_handler.server_exception.ServerIOException;
import com.word_trainer.repository.LexemeRepository;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.interfaces.TranslationService;
import com.word_trainer.services.mapping.LexemeMapperService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.word_trainer.services.utilities.CollectionUtilities.mergeCollections;

@Service
@RequiredArgsConstructor
public class LexemeServiceImpl implements LexemeService {

    private final LexemeRepository repository;

    private final TranslationService translationService;

    private final LexemeMapperService lexemeMapperService;

    double OLD_RESULTS_PROPORTION = 0.6;

    @Override
    public int getCountOfCreatedLexemeFromFile(LexemesFileDto dto) {
        checkFile(dto.getFile());
        return getCountOfCreatedLexemesFromExcelRowInFile(dto);
    }

    @Override
    public void createLexeme(LexemeDto dto) {
        createLexemeByLexemeDto(dto);
    }

    @Override
    public ResponseLexemesDto getLexemes(int count, Language sourceLanguage,
                                         Language targetLanguage, User currectUser) {
        List<Lexeme> lexemesWithResult = getFilteredLexemes(
                currectUser,
                sourceLanguage,
                targetLanguage);

        List<UUID> lexemesIdWithResult = lexemesWithResult.stream()
                .map(Lexeme::getId)
                .toList();

        Pageable pageable = PageRequest.of(0, count);
        List<Lexeme> lexemesWithoutResult = repository.findRandomLexemes(
                pageable,
                sourceLanguage,
                targetLanguage,
                lexemesIdWithResult);

        List<Lexeme> lexemes = mergeCollections(
                lexemesWithResult,
                lexemesWithoutResult,
                count,
                OLD_RESULTS_PROPORTION);

        return getResponseLexemesDto(sourceLanguage, targetLanguage, lexemes);
    }

    private List<Lexeme> getFilteredLexemes(User user,
                                            Language sourceLanguage,
                                            Language targetLanguage) {
        return user.getUserResult().stream()
                .filter(r -> r.getSourceLanguage().equals(sourceLanguage) &&
                        r.getTargetLanguage().equals(targetLanguage) &&
                        r.getIsActive()
                )
                .sorted(Comparator
                        .comparing(UserLexemeResult::getUpdatedAt)
                        .thenComparing(UserLexemeResult::getAttempts)
                        .thenComparing(r ->
                                r.getAttempts() == 0 ? 0 : (double) r.getSuccessfulAttempts() / r.getAttempts()
                        )
                )
                .map(UserLexemeResult::getLexeme)
                .toList();
    }

    @Override
    public Lexeme getLexemesById(UUID lexemeId) {
        return repository.findById(lexemeId)
                .orElseThrow(() -> new LexemeNotFoundException(lexemeId));
    }

    private ResponseLexemesDto getResponseLexemesDto(Language sourceLanguage,
                                                     Language targetLanguage,
                                                     List<Lexeme> lexemes) {
        List<ResponseTranslationDto> responseTranslationDtos = lexemes.stream()
                .map(l -> {
                            LexemeTranslationDto dto = LexemeTranslationDto.builder()
                                    .sourceLanguage(sourceLanguage)
                                    .targetLanguage(targetLanguage)
                                    .lexeme(l)
                                    .build();
                            return lexemeMapperService.toResponseTranslationDto(dto);
                        }
                ).toList();
        return ResponseLexemesDto.builder()
                .sourceLanguage(sourceLanguage)
                .targetLanguage(targetLanguage)
                .translations(responseTranslationDtos)
                .build();
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

    @Override
    @Transactional
    public Lexeme createNewLexeme(LexemeDto dto) {
        Lexeme newLexeme = Lexeme.builder()
                .type(dto.getType())
                .translations(new HashSet<>())
                .build();
        Lexeme savedLexeme = repository.save(newLexeme);
        Translation sourceTranslation = Translation.builder()
                .meaning(dto.getSourceMeaning())
                .language(dto.getSourceLanguage())
                .lexeme(savedLexeme)
                .build();
        Translation targetTranslation = Translation.builder()
                .meaning(dto.getTargetMeaning())
                .language(dto.getTargetLanguage())
                .lexeme(savedLexeme)
                .build();
        savedLexeme.getTranslations().add(sourceTranslation);
        savedLexeme.getTranslations().add(targetTranslation);
        return repository.save(savedLexeme);
    }

    private void updateLexeme(Translation existingTranslation, LexemeDto dto) {
        Lexeme currentLexeme = existingTranslation.getLexeme();
        translationService.updateTargetTranslation(dto, currentLexeme);
    }
}
