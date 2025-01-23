package com.word_trainer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.response.ResponseLexemesDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.Translation;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;
import com.word_trainer.repository.TranslationRepository;
import com.word_trainer.repository.UserLexemeResultRepository;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.security.contstants.Role;
import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.mapping.UserMapperService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Lexeme controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class LexemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TranslationRepository translationRepository;

    @Autowired
    private UserLexemeResultRepository userLexemeResultRepository;

    @Autowired
    private LexemeService lexemeService;

    @Autowired
    private UserMapperService mapperService;

    private ObjectMapper mapper = new ObjectMapper();

    private String accessToken1;
    private Long currentUserId1;

    private String adminAccessToken;
    private Long currentAdminId;

    private static final String USER1_EMAIL = "Test1" + "@example.com";
    private static final String USER1_PASSWORD = "Querty123!";
    private static final String TEST_USER_NAME_1 = "TestName1";

    private static final String ADMIN_EMAIL = "Admin" + "@example.com";
    private static final String ADMIN_PASSWORD = "Querty123!";
    private static final String TEST_ADMIN_NAME = "Admin TestName";

    private static final String LOGIN_URL = "/v1/auth/login";
    private static final String LEXEME_FILE_URL = "/v1/lexeme/file";
    private static final String LEXEME_URL = "/v1/lexeme";


    private void loginUser1() throws Exception {
        TokenResponseDto responseDto = loginUser(USER1_EMAIL, TEST_USER_NAME_1, USER1_PASSWORD);
        accessToken1 = responseDto.getAccessToken();
        currentUserId1 = responseDto.getUserId();
    }

    @Transactional
    protected void loginAdmin() throws Exception {
        UserRegistrationDto dto = UserRegistrationDto
                .builder()
                .email(ADMIN_EMAIL)
                .userName(TEST_ADMIN_NAME)
                .password(ADMIN_PASSWORD)
                .build();
        User admin = userRepository.save(mapperService.toEntity(dto));
        admin.setRole(Role.ROLE_ADMIN);
        String dtoJson = mapper.writeValueAsString(
                LoginDto.builder()
                        .email(ADMIN_EMAIL)
                        .password(ADMIN_PASSWORD)
                        .build());
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        TokenResponseDto responseDto = mapper.readValue(jsonResponse, TokenResponseDto.class);
        adminAccessToken = responseDto.getAccessToken();
        currentAdminId = responseDto.getUserId();
    }

    private TokenResponseDto loginUser(String email, String name, String password) throws Exception {
        UserRegistrationDto dto = UserRegistrationDto
                .builder()
                .email(email)
                .userName(name)
                .password(password)
                .build();
        userRepository.save(mapperService.toEntity(dto));
        String dtoJson = mapper.writeValueAsString(
                LoginDto.builder()
                        .email(email)
                        .password(password)
                        .build());
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        return mapper.readValue(jsonResponse, TokenResponseDto.class);
    }

    private List<Lexeme> createNewLexemes() {
        return createNewLexemes(10);
    }

    private List<Lexeme> createNewLexemes(int count) {
        List<Lexeme> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LexemeDto dto = LexemeDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .sourceMeaning(String.format("Test EN %d", i))
                    .targetMeaning(String.format("Test DE %d", i))
                    .type(LexemeType.WORD)
                    .build();
            result.add(lexemeService.createNewLexeme(dto));
        }
        return result;
    }

    private UserLexemeResult createNewUserLexemeResults(User user, Lexeme lexeme, int attempts, int successfulAttempts) {

        UserLexemeResult result = UserLexemeResult.builder()
                .sourceLanguage(Language.EN)
                .targetLanguage(Language.DE)
                .attempts(attempts)
                .successfulAttempts(successfulAttempts)
                .user(user)
                .lexeme(lexeme)
                .build();
        return userLexemeResultRepository.save(result);
    }


    @Nested
    @DisplayName("POST /v1/lexeme/file")
    class CreateLexemesByFileTests {

        @ParameterizedTest
        @CsvSource({"5", "0"})
        public void create_lexemes_by_excel_file_status_201(int countOfRow) throws Exception {
            loginAdmin();

            List<String> meanings = new ArrayList<>();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("LexemesTest");
            for (int i = 0; i < countOfRow; i++) {
                String sourceText = String.format("Test EN %d", i);
                String targetText = String.format("Test DE %d", i);
                String typeText = String.format("Test Type  %d", i);
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(sourceText);
                row.createCell(1).setCellValue(targetText);
                row.createCell(2).setCellValue(typeText);
                meanings.add(sourceText);
                meanings.add(targetText);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray()
            );

            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)))
                    .andExpect(jsonPath("$.message", containsString(String.valueOf(countOfRow))));

            List<Translation> translations = translationRepository.findByMeaningIn(meanings);

            Set<UUID> lexemesIDs = new HashSet<>();
            translations.forEach(t -> {
                lexemesIDs.add(t.getLexeme().getId());
                assertTrue(meanings.contains(t.getMeaning()));
            });
            assertEquals(countOfRow * 2, translations.size());
            assertEquals(countOfRow, lexemesIDs.size());
        }

        @Test
        public void create_lexemes_by_excel_file_status_201_when_source_meaning_is_already_exists() throws Exception {
            loginAdmin();

            List<String> meanings = new ArrayList<>();
            int countOfRow = 5;

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("LexemesTest");
            for (int i = 0; i < countOfRow; i++) {
                String sourceText = String.format("Test EN %d", i);
                String targetText = String.format("Test DE %d", i);
                String typeText = String.format("Test Type  %d", i);
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(sourceText);
                row.createCell(1).setCellValue(targetText);
                row.createCell(2).setCellValue(typeText);
                meanings.add(sourceText);
                meanings.add(targetText);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray()
            );

            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isCreated());

            Workbook workbook1 = new XSSFWorkbook();
            Sheet sheet1 = workbook1.createSheet("LexemesTest1");
            for (int i = 0; i < countOfRow; i++) {
                String sourceText = String.format("Test EN %d", i);
                String targetText = String.format("Test DE %d", i + 10);
                String typeText = String.format("Test Type  %d", i + 10);
                Row row = sheet1.createRow(i);
                row.createCell(0).setCellValue(sourceText);
                row.createCell(1).setCellValue(targetText);
                row.createCell(2).setCellValue(typeText);
                meanings.remove(String.format("Test DE %d", i));
                meanings.add(String.format("Test DE %d, Test DE %d", i, i + 10));
            }
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            workbook1.write(outputStream1);
            workbook1.close();

            MockMultipartFile mockFile1 = new MockMultipartFile(
                    "file",
                    "lexemes1.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream1.toByteArray()
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile1)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)))
                    .andExpect(jsonPath("$.message", containsString(String.valueOf(countOfRow))));

            List<Translation> translations = translationRepository.findByMeaningIn(meanings);

            Set<UUID> lexemesIDs = new HashSet<>();
            translations.forEach(t -> {
                lexemesIDs.add(t.getLexeme().getId());
                assertTrue(meanings.contains(t.getMeaning()));
            });
            assertEquals(countOfRow * 2, translations.size());
            assertEquals(countOfRow, lexemesIDs.size());
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_file_is_empty() throws Exception {
            loginAdmin();

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    new byte[0]
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_request_is_empty() throws Exception {
            loginAdmin();
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_file_format_is_wrong() throws Exception {
            loginAdmin();
            byte[] randomBytes = new byte[256];
            new Random().nextBytes(randomBytes);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.txt",
                    "text/plain",
                    randomBytes
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_language_is_wrong() throws Exception {
            loginAdmin();
            byte[] randomBytes = new byte[256];
            new Random().nextBytes(randomBytes);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    randomBytes
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "test")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_language_is_null() throws Exception {
            loginAdmin();
            byte[] randomBytes = new byte[256];
            new Random().nextBytes(randomBytes);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    randomBytes
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")

                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void create_lexemes_by_excel_file_status_400_when_file_is_null() throws Exception {
            loginAdmin();
            mockMvc.perform(multipart(LEXEME_FILE_URL)

                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void create_lexemes_by_excel_file_status_401_when_user_isnt_authorized() throws Exception {

            byte[] randomBytes = new byte[256];
            new Random().nextBytes(randomBytes);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    randomBytes
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "testToken"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void create_lexemes_by_excel_file_status_403_when_user_dont_have_right() throws Exception {
            loginUser1();
            byte[] randomBytes = new byte[256];
            new Random().nextBytes(randomBytes);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "lexemes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    randomBytes
            );
            mockMvc.perform(multipart(LEXEME_FILE_URL)
                            .file(mockFile)
                            .param("sourceLanguage", "EN")
                            .param("targetLanguage", "DE")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }

    @Nested
    @DisplayName("POST /v1/lexeme")
    class CreateLexemeByDtoTests {

        @Test
        public void create_lexeme_by_dto_status_201() throws Exception {
            loginAdmin();
            List<String> meanings = new ArrayList<>();
            String sourceValue = "test En";
            String targetValue = "test De";
            meanings.add(sourceValue);
            meanings.add(targetValue);
            LexemeDto dto = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();
            String jsonDto = mapper.writeValueAsString(dto);
            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

            List<Translation> translations = translationRepository.findByMeaningIn(meanings);

            Set<UUID> lexemesIDs = new HashSet<>();
            translations.forEach(t -> {
                lexemesIDs.add(t.getLexeme().getId());
                assertTrue(meanings.contains(t.getMeaning()));
                assertEquals(t.getLexeme().getType(), LexemeType.WORD);
            });
            assertEquals(2, translations.size());
            assertEquals(1, lexemesIDs.size());
        }

        @Test
        public void create_lexeme_by_dto_status_400_when_dto_is_null() throws Exception {
            loginAdmin();
            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when languages are wrong[{arguments}]")
        @CsvSource({
                "\"sourceLanguage\":\"EN\", \"sourceLanguage\":\"INVALID_LANGUAGE\"",
                "\"targetLanguage\":\"DE\", \"targetLanguage\":\"INVALID_LANGUAGE\""
        })
        public void create_lexeme_by_dto_status_400_when_language_is_wrong(String language,
                                                                           String wrongLanguage) throws Exception {
            loginAdmin();
            String sourceValue = "test En";
            String targetValue = "test De";
            LexemeDto dto = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();
            String jsonDto = mapper.writeValueAsString(dto)
                    .replace(language, wrongLanguage);

            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when dto values is wrong[{arguments}]")
        @MethodSource("incorrectDtoValues")
        public void create_lexeme_by_dto_status_400_when_dto_values_is_wrong(LexemeDto dto) throws Exception {
            loginAdmin();

            String jsonDto = mapper.writeValueAsString(dto);
            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        private static Stream<Arguments> incorrectDtoValues() {
            String sourceValue = "test En";
            String targetValue = "test De";
            LexemeDto dto1 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(null)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto2 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(null)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto3 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto4 = LexemeDto.builder()
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto5 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto6 = LexemeDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto7 = LexemeDto.builder()
                    .build();

            LexemeDto dto8 = LexemeDto.builder()
                    .sourceMeaning("")
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto9 = LexemeDto.builder()
                    .sourceMeaning("   ")
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto10 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning("")
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto11 = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning("   ")
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();

            LexemeDto dto12 = LexemeDto.builder()
                    .sourceMeaning("")
                    .targetMeaning("   ")
                    .sourceLanguage(null)
                    .type(LexemeType.WORD)
                    .build();

            return Stream.of(
                    Arguments.of(dto1),
                    Arguments.of(dto2),
                    Arguments.of(dto3),
                    Arguments.of(dto4),
                    Arguments.of(dto5),
                    Arguments.of(dto6),
                    Arguments.of(dto7),
                    Arguments.of(dto8),
                    Arguments.of(dto9),
                    Arguments.of(dto10),
                    Arguments.of(dto11),
                    Arguments.of(dto12)
            );
        }

        @Test
        public void create_lexeme_by_dto_status_401_when_user_isnt_authorized() throws Exception {

            String sourceValue = "test En";
            String targetValue = "test De";

            LexemeDto dto = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();
            String jsonDto = mapper.writeValueAsString(dto);
            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void create_lexeme_by_dto_status_403_when_user_dont_have_right() throws Exception {
            loginUser1();
            String sourceValue = "test En";
            String targetValue = "test De";

            LexemeDto dto = LexemeDto.builder()
                    .sourceMeaning(sourceValue)
                    .targetMeaning(targetValue)
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .type(LexemeType.WORD)
                    .build();
            String jsonDto = mapper.writeValueAsString(dto);

            mockMvc.perform(multipart(LEXEME_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }

    @Nested
    @DisplayName("GET /v1/lexeme")
    class GetLexemesTests {

        @ParameterizedTest(name = "Test {index}: Get lexemes with status 200 [{arguments}]")
        @CsvSource({
                "10, 10, 15, 8, 6",
                "10, 10, 15, 2, 2",
                "10, 10, 15, 14, 9",
                "10, 5, 5, 2, 2",
                "10, 3, 3, 0, 0",
                "10, 10, 15, 0, 0",
                "10, 10, 15, 15, 10",
                "10, 0, 0, 0, 0",
        })
        public void get_lexemes_status_200(
                int countOfResults,
                int countOfRealResults,
                int countOfLexeme,
                int countOfLexemeWithResult,
                int countOfNewLexeme) throws Exception {
            loginUser1();
            int attempts = 4;
            int successfulAttempts = 3;
            String sourceLanguage = "EN";
            String targetLanguage = "DE";

            User user = userRepository.findById(currentUserId1).get();

            List<Lexeme> createdLexemes = createNewLexemes(countOfLexeme);
            List<UUID> createdLexemesWithResultsIds = new ArrayList<>();
            UUID idInActiveLexemeResult = null;
            for (int i = 0; i < countOfLexemeWithResult; i++) {
                UserLexemeResult createdUserLexemeResult = createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
                user.getUserResult().add(createdUserLexemeResult);
                createdLexemesWithResultsIds.add(createdLexemes.get(i).getId());
            }
            MvcResult result = mockMvc.perform(get(LEXEME_URL)
                            .param("count", String.valueOf(countOfResults))
                            .param("sourceLanguage", sourceLanguage)
                            .param("targetLanguage", targetLanguage)
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sourceLanguage", is(sourceLanguage)))
                    .andExpect(jsonPath("$.targetLanguage", is(targetLanguage)))
                    .andExpect(jsonPath("$.translations").isArray())
                    .andExpect(jsonPath("$.translations", hasSize(countOfRealResults)))
                    .andReturn();
            String jsonResponse = result.getResponse().getContentAsString();
            ResponseLexemesDto responseDto = mapper.readValue(jsonResponse, ResponseLexemesDto.class);

            long countOfNewLexemeInResult = responseDto.getTranslations()
                    .stream()
                    .filter(t -> createdLexemesWithResultsIds.contains(t.getLexemeId()))
                    .count();

            assertEquals(countOfNewLexeme, countOfNewLexemeInResult);
        }

        @ParameterizedTest(name = "Test {index}: Get status 400 when parameters are wrong[{arguments}]")
        @CsvSource({
                "0 , EN, DE",
                "-20, EN, DE",
                "51, EN, DE",
                "10, ,  DE ",
                "10,  EN,   ",
                "10, , ",
        })
        public void get_lexemes_status_400_when_request_parameters_are_wrong(
                int countOfResults,
                String sourceLanguage,
                String targetLanguage
        ) throws Exception {
            loginUser1();

            if (sourceLanguage != null && sourceLanguage.isBlank()) {
                sourceLanguage = null;
            }
            if (targetLanguage != null && targetLanguage.isBlank()) {
                targetLanguage = null;
            }
            mockMvc.perform(get(LEXEME_URL)
                            .param("count", String.valueOf(countOfResults))
                            .param("sourceLanguage", sourceLanguage)
                            .param("targetLanguage", targetLanguage)
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @ParameterizedTest(name = "Test {index}: Get status 400 when languages parameters are wrong[{arguments}]")
        @CsvSource({
                "Test1, DE",
                "EN, Test1",
                "Test1, Test2",
        })
        public void get_lexemes_status_400_when_request_languages_parameters_are_wrong(
                String sourceLanguage,
                String targetLanguage
        ) throws Exception {
            loginUser1();
            int countOfResults = 10;
            mockMvc.perform(get(LEXEME_URL)
                            .param("count", String.valueOf(countOfResults))
                            .param("sourceLanguage", sourceLanguage)
                            .param("targetLanguage", targetLanguage)
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void get_lexemes_status_401_when_user_is_not_authorized() throws Exception {
            int countOfResults = 10;
            String sourceLanguage = "EN";
            String targetLanguage = "DE";

            mockMvc.perform(get(LEXEME_URL)
                            .param("count", String.valueOf(countOfResults))
                            .param("sourceLanguage", sourceLanguage)
                            .param("targetLanguage", targetLanguage)
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "Test Token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }
}
