package com.word_trainer.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemeTranslationDto;
import com.word_trainer.domain.dto.response.PageResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.response.ResponseTranslationDto;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.ResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.user_lexeme_result.UpdateStatusUserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserResultsDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.Lexeme;
import com.word_trainer.domain.entity.User;
import com.word_trainer.domain.entity.UserLexemeResult;
import com.word_trainer.repository.UserLexemeResultRepository;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.security.contstants.Role;
import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.mapping.LexemeMapperService;
import com.word_trainer.services.mapping.UserMapperService;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("User LexemeResult controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class UserLexemeResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLexemeResultRepository userLexemeResultRepository;

    @Autowired
    private LexemeService lexemeService;

    @Autowired
    private UserMapperService mapperService;

    @Autowired
    private LexemeMapperService lexemeMapperService;

    private final ObjectMapper mapper = new ObjectMapper();

    private String accessToken1;
    private Long currentUserId1;

    private String accessToken2;
    private Long currentUserId2;

    private String adminAccessToken;
    private Long currentAdminId;

    private static final String USER1_EMAIL = "Test1" + "@example.com";
    private static final String USER1_PASSWORD = "Querty123!";
    private static final String TEST_USER_NAME_1 = "TestName1";

    private static final String USER2_EMAIL = "Test2" + "@example.com";
    private static final String USER2_PASSWORD = "Querty123!";
    private static final String TEST_USER_NAME_2 = "TestName2";

    private static final String ADMIN_EMAIL = "Admin" + "@example.com";
    private static final String ADMIN_PASSWORD = "Querty123!";
    private static final String TEST_ADMIN_NAME = "Admin TestName";

    private static final String LOGIN_URL = "/v1/auth/login";
    private static final String USER_LEXEMES_RESULT_URL = "/v1/users/lexeme-results";
    private static final String USER_LEXEMES_RESULT_TRANSLATION_URL = "/v1/users/lexeme-results/translations";
    private static final String USER_LEXEMES_RESULT_UPDATE_STATUS_URL = "/v1/users/lexeme-results/active";

    private void loginUser1() throws Exception {
        TokenResponseDto responseDto = loginUser(USER1_EMAIL, TEST_USER_NAME_1, USER1_PASSWORD);
        accessToken1 = responseDto.getAccessToken();
        currentUserId1 = responseDto.getUserId();
    }

    private void loginUser2() throws Exception {
        TokenResponseDto responseDto = loginUser(USER2_EMAIL, TEST_USER_NAME_2, USER2_PASSWORD);
        accessToken2 = responseDto.getAccessToken();
        currentUserId2 = responseDto.getUserId();
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

    private UserLexemeResult createNewUserLexemeResults(
            User user,
            Lexeme lexeme,
            int attempts,
            int successfulAttempts) {
        return createNewUserLexemeResults(
                user,
                lexeme,
                attempts,
                successfulAttempts,
                Language.EN,
                Language.DE);
    }

    private UserLexemeResult createNewUserLexemeResults(
            User user,
            Lexeme lexeme,
            int attempts,
            int successfulAttempts,
            Language sourceLanguage,
            Language targetLanguage) {
        UserLexemeResult result = UserLexemeResult.builder()
                .sourceLanguage(sourceLanguage)
                .targetLanguage(targetLanguage)
                .attempts(attempts)
                .successfulAttempts(successfulAttempts)
                .user(user)
                .lexeme(lexeme)
                .build();
        return userLexemeResultRepository.save(result);
    }

    @Nested
    @DisplayName("POST " + USER_LEXEMES_RESULT_URL)
    public class UpsertUserLexemeResultTests {

        @Test
        public void upsert_lexeme_result_of_user_status_200() throws Exception {
            loginUser1();
            int attempts = 6;
            int successfulAttempts = 3;
            User user = userRepository.findById(currentUserId1).get();

            List<Lexeme> createdLexemes = createNewLexemes();
            for (int i = 0; i < 2; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
            }

            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                UserResultsDto dto = UserResultsDto.builder()
                        .lexemeId(createdLexemes.get(i).getId())
                        .attempts(3)
                        .successfulAttempts(2)
                        .isActive(false)
                        .build();
                userResultsDtoSet.add(dto);
            }

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

            List<UserLexemeResult> upsertedResult = userLexemeResultRepository.findAll();
            assertEquals(4, upsertedResult.size());

            AtomicInteger countUpdateResult = new AtomicInteger();
            AtomicInteger countCreateResult = new AtomicInteger();
            upsertedResult.forEach(r -> {
                assertFalse(r.getIsActive());
                if (r.getAttempts() == 9 && r.getSuccessfulAttempts() == 5) {
                    countUpdateResult.getAndIncrement();
                }
                if (r.getAttempts() == 3 && r.getSuccessfulAttempts() == 2) {
                    countCreateResult.getAndIncrement();
                }
            });
            assertEquals(countUpdateResult.get(), 2);
            assertEquals(countCreateResult.get(), 2);
        }

        @Test
        public void upsert_lexeme_result_of_user_status_200_when_another_user_have_results() throws Exception {
            loginUser1();
            loginUser2();

            int attempts = 6;
            int successfulAttempts = 3;
            User user = userRepository.findById(currentUserId2).get();

            List<Lexeme> createdLexemes = createNewLexemes();
            for (int i = 0; i < 2; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
            }

            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                UserResultsDto dto = UserResultsDto.builder()
                        .lexemeId(createdLexemes.get(i).getId())
                        .attempts(3)
                        .successfulAttempts(2)
                        .build();
                userResultsDtoSet.add(dto);
            }

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

            List<UserLexemeResult> upsertedResult = userLexemeResultRepository.findAll();
            assertEquals(6, upsertedResult.size());

            AtomicInteger countUpdateResult = new AtomicInteger();
            AtomicInteger countCreateResult = new AtomicInteger();
            AtomicInteger countConstResult = new AtomicInteger();
            upsertedResult.forEach(r -> {
                if (r.getAttempts() == 9 && r.getSuccessfulAttempts() == 5) {
                    countUpdateResult.getAndIncrement();
                }
                if (r.getAttempts() == 3 && r.getSuccessfulAttempts() == 2) {
                    countCreateResult.getAndIncrement();
                }
                if (r.getAttempts() == 6 && r.getSuccessfulAttempts() == 3) {
                    countConstResult.getAndIncrement();
                }
            });
            assertEquals(countUpdateResult.get(), 0);
            assertEquals(countCreateResult.get(), 4);
            assertEquals(countConstResult.get(), 2);

            long countUser1Result = upsertedResult.stream()
                    .filter(r -> r.getUser().getId().equals(currentUserId1))
                    .count();
            long countUser2Result = upsertedResult.stream()
                    .filter(r -> r.getUser().getId().equals(currentUserId2))
                    .count();
            assertEquals(countUser1Result, 4);
            assertEquals(countUser2Result, 2);
        }

        @Test
        public void upsert_lexeme_result_of_user_status_400_when_dto_is_null() throws Exception {
            loginUser1();
            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when count of attempts is wrong[{arguments}]")
        @MethodSource("incorrectDtoValues")
        public void upsert_lexeme_result_of_user_status_400_when_count_of_attempts_is_wrong(
                UserResultsDto userResultsDto) throws Exception {
            loginUser1();

            List<Lexeme> createdLexemes = createNewLexemes(1);

            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();

            userResultsDto.setLexemeId(createdLexemes.get(0).getId());

            userResultsDtoSet.add(userResultsDto);

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        private static Stream<Arguments> incorrectDtoValues() {
            return Stream.of(
                    Arguments.of(UserResultsDto.builder()
                            .attempts(0)
                            .successfulAttempts(2)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .attempts(-1)
                            .successfulAttempts(4)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .attempts(4)
                            .successfulAttempts(-4)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .attempts(5)
                            .successfulAttempts(12)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .attempts(-5)
                            .successfulAttempts(-2)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .successfulAttempts(12)
                            .build()),
                    Arguments.of(UserResultsDto.builder()
                            .build())
            );
        }

        @Test
        public void upsert_lexeme_result_of_user_status_400_when_lexeme_id_is_null() throws Exception {
            loginUser1();
            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();

            UserResultsDto dto = UserResultsDto.builder()
                    .attempts(3)
                    .successfulAttempts(2)
                    .build();
            userResultsDtoSet.add(dto);

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when languages are wrong[{arguments}]")
        @CsvSource({
                "\"sourceLanguage\":\"EN\", \"sourceLanguage\":\"INVALID_LANGUAGE\"",
                "\"targetLanguage\":\"DE\", \"targetLanguage\":\"INVALID_LANGUAGE\""
        })
        public void upsert_lexeme_result_of_user_status_400_when_language_is_wrong(String language,
                                                                                   String wrongLanguage) throws Exception {
            loginUser1();
            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();

            List<Lexeme> createdLexemes = createNewLexemes(1);
            UserResultsDto dto = UserResultsDto.builder()
                    .lexemeId(createdLexemes.get(0).getId())
                    .attempts(3)
                    .successfulAttempts(2)
                    .build();
            userResultsDtoSet.add(dto);

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto)
                    .replace(language, wrongLanguage);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: Get with status 400 when languages are wrong[{arguments}]")
        @MethodSource("incorrectUserLexemeResultDtoValues")
        public void upsert_lexeme_result_of_user_status_400_when_UserLexemeResultDto_params_is_wrong(UserLexemeResultDto resultDto) throws Exception {
            loginUser1();

            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        private static Stream<Arguments> incorrectUserLexemeResultDtoValues() {
            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();

            UserResultsDto dto = UserResultsDto.builder()
                    .lexemeId(UUID.randomUUID())
                    .attempts(3)
                    .successfulAttempts(2)
                    .build();
            userResultsDtoSet.add(dto);

            return Stream.of(
                    Arguments.of(UserLexemeResultDto.builder()
                            .targetLanguage(Language.DE)
                            .resultDtos(userResultsDtoSet)
                            .build()),
                    Arguments.of(UserLexemeResultDto.builder()
                            .sourceLanguage(Language.EN)
                            .resultDtos(userResultsDtoSet)
                            .build()),
                    Arguments.of(UserLexemeResultDto.builder()
                            .sourceLanguage(Language.EN)
                            .targetLanguage(Language.DE)
                            .build()),
                    Arguments.of(UserLexemeResultDto.builder()
                            .resultDtos(userResultsDtoSet)
                            .build()),
                    Arguments.of(UserLexemeResultDto.builder()
                            .build())
            );
        }

        @Test
        public void upsert_lexeme_result_of_user_status_401_user_is_not_authorized() throws Exception {

            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                UserResultsDto dto = UserResultsDto.builder()
                        .lexemeId(UUID.randomUUID())
                        .attempts(3)
                        .successfulAttempts(2)
                        .build();
                userResultsDtoSet.add(dto);
            }

            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

        }

        @Test
        public void upsert_lexeme_result_of_user_status_404_when_lexeme_is_not_found() throws Exception {
            loginUser1();
            int attempts = 6;
            int successfulAttempts = 3;
            User user = userRepository.findById(currentUserId1).get();

            List<Lexeme> createdLexemes = createNewLexemes();
            for (int i = 0; i < 2; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
            }

            Set<UserResultsDto> userResultsDtoSet = new HashSet<>();

            UserResultsDto dto = UserResultsDto.builder()
                    .lexemeId(UUID.randomUUID())
                    .attempts(3)
                    .successfulAttempts(2)
                    .build();
            userResultsDtoSet.add(dto);
            UserLexemeResultDto resultDto = UserLexemeResultDto.builder()
                    .sourceLanguage(Language.EN)
                    .targetLanguage(Language.DE)
                    .resultDtos(userResultsDtoSet)
                    .build();
            String jsonDto = mapper.writeValueAsString(resultDto);

            mockMvc.perform(multipart(USER_LEXEMES_RESULT_URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }

    @Nested
    @DisplayName("GET " + USER_LEXEMES_RESULT_URL)
    public class GetUserStudyStatisticsTests {

        static class Counts {
            private int result;
            private int attempts;
            private int successfulAttempts;
            private final Language sourceLanguage;
            private final Language targetLanguage;

            public Counts(Language sourceLanguage, Language targetLanguage) {
                this.sourceLanguage = sourceLanguage;
                this.targetLanguage = targetLanguage;
                this.result = 0;
                this.attempts = 0;
                this.successfulAttempts = 0;
            }

            public void incrementStats(int attempts, int successfulAttempts) {
                this.result += 1;
                this.attempts += attempts;
                this.successfulAttempts += successfulAttempts;
            }

            public ResponseUserResultsDto responseUserResultsDtoFactory() {
                return ResponseUserResultsDto.builder()
                        .sourceLanguage(this.sourceLanguage)
                        .targetLanguage(this.targetLanguage)
                        .countOfResult(this.result)
                        .countOfAttempts(this.attempts)
                        .countOfSuccessfulAttempts(this.successfulAttempts)
                        .build();
            }
        }

        @Test
        public void get_user_study_statistics_status_200() throws Exception {
            loginUser1();

            int attemptsEnDe = 6;
            int successfulAttemptsEnDe = 3;
            int attemptsEnUkr = 16;
            int successfulAttemptsEnUkr = 10;
            int attemptsDeUkr = 33;
            int successfulAttemptsDeUkr = 27;
            User user = userRepository.findById(currentUserId1).get();

            List<ResponseUserResultsDto> expectedUserResults = new ArrayList<>();

            List<Lexeme> createdLexemes = createNewLexemes(10);
            Counts countsEnDE = new Counts(Language.EN, Language.DE);
            Counts countsEnUkr = new Counts(Language.EN, Language.UKR);
            Counts countsDeUkr = new Counts(Language.DE, Language.UKR);

            for (int i = 0; i < 10; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attemptsEnDe + i,
                        successfulAttemptsEnDe + i);
                countsEnDE.incrementStats(attemptsEnDe + i, successfulAttemptsEnDe + i);
                if (i < 6) {
                    createNewUserLexemeResults(user,
                            createdLexemes.get(i),
                            attemptsEnUkr + i,
                            successfulAttemptsEnUkr + i,
                            Language.EN, Language.UKR);
                    countsEnUkr.incrementStats(attemptsEnUkr + i, successfulAttemptsEnUkr + i);
                }
                if (i < 9) {
                    createNewUserLexemeResults(user,
                            createdLexemes.get(i),
                            attemptsDeUkr + i,
                            successfulAttemptsDeUkr + i,
                            Language.DE, Language.UKR);
                    countsDeUkr.incrementStats(attemptsDeUkr + i, successfulAttemptsDeUkr + i);
                }
            }

            ResponseUserResultsDto resultEnDe = countsEnDE.responseUserResultsDtoFactory();
            ResponseUserResultsDto resultEnUkr = countsEnUkr.responseUserResultsDtoFactory();
            ResponseUserResultsDto resultDeUkr = countsDeUkr.responseUserResultsDtoFactory();

            expectedUserResults.add(resultEnDe);
            expectedUserResults.add(resultEnUkr);
            expectedUserResults.add(resultDeUkr);

            loginUser2();

            int attempts2 = 12;
            int successfulAttempts2 = 5;
            User user2 = userRepository.findById(currentUserId2).get();
            for (int i = 0; i < 5; i++) {
                createNewUserLexemeResults(user2,
                        createdLexemes.get(i),
                        attempts2 + i,
                        successfulAttempts2 + i);
            }

            MvcResult result = mockMvc.perform(get(USER_LEXEMES_RESULT_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();

            List<ResponseUserResultsDto> responseList = mapper.readValue(
                    jsonResponse,
                    new TypeReference<List<ResponseUserResultsDto>>() {
                    }
            );
            assertEquals(responseList.size(), 3);
            assertEquals(expectedUserResults, responseList);
        }

        @Test
        public void get_user_study_statistics_status_200_when_result_is_empty() throws Exception {
            loginUser1();

            loginUser2();

            int attempts2 = 12;
            int successfulAttempts2 = 5;
            User user2 = userRepository.findById(currentUserId2).get();
            List<Lexeme> createdLexemes = createNewLexemes(10);
            for (int i = 0; i < 5; i++) {
                createNewUserLexemeResults(user2,
                        createdLexemes.get(i),
                        attempts2 + i,
                        successfulAttempts2 + i);
            }

            MvcResult result = mockMvc.perform(get(USER_LEXEMES_RESULT_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();

            List<ResponseUserResultsDto> responseList = mapper.readValue(
                    jsonResponse,
                    new TypeReference<List<ResponseUserResultsDto>>() {
                    }
            );
            assertEquals(responseList.size(), 0);
            assertTrue(responseList.isEmpty());
        }

        @Test
        public void get_user_study_statistics_status_401_when_user_unauthorized() throws Exception {
            mockMvc.perform(get(USER_LEXEMES_RESULT_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

        }
    }

    @Nested
    @DisplayName("GET " + USER_LEXEMES_RESULT_TRANSLATION_URL)
    public class GetTranslationsTests {

        private List<ResponseUserResultsTranslationDto> getResponseUserResultsTranslationDtoList(
                int totalElements,
                String sourceLanguage,
                String targetLanguage) {

            if (totalElements == 0) return new ArrayList<>();
            int attemptsEnDe = 6;
            int successfulAttemptsEnDe = 3;

            Language sourceLanguageEnum = Language.valueOf(sourceLanguage);
            Language targetLanguageEnum = Language.valueOf(targetLanguage);

            Random random = new Random();

            List<Lexeme> createdLexemes = createNewLexemes(totalElements + 3);
            User user = userRepository.findById(currentUserId1).get();

            List<ResponseUserResultsTranslationDto> resultList = new ArrayList<>();

            for (int i = 0; i < totalElements; i++) {
                Lexeme lexeme = createdLexemes.get(i);
                int attempts = attemptsEnDe + i;
                int successfulAttempts = successfulAttemptsEnDe + i;

                createNewUserLexemeResults(user, lexeme, attempts, successfulAttempts, sourceLanguageEnum, targetLanguageEnum);
                ResponseTranslationDto dto = lexemeMapperService.toResponseTranslationDto(
                        LexemeTranslationDto.builder()
                                .lexeme(lexeme)
                                .sourceLanguage(sourceLanguageEnum)
                                .targetLanguage(targetLanguageEnum)
                                .build()
                );
                ResponseUserResultsTranslationDto translationDto = ResponseUserResultsTranslationDto.from(
                        dto, random.nextBoolean(), attempts, successfulAttempts);
                resultList.add(translationDto);
            }
            for (int i = 0; i < 3; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        65,
                        47,
                        Language.UKR, Language.RU);
            }
            return resultList;
        }

        private int getContentSize(int totalElements, int size, boolean isLast) {

            if (totalElements == 0) return 0;

            return isLast ? (totalElements % size == 0 ? size : totalElements % size) : size;
        }

        @ParameterizedTest(name = "Test {index}: Get translation with status 200 [{arguments}]")
        @CsvSource({
                "0, 10, attempts, true, 22, EN, DE",
                "1, 5, attempts, false, 22, UKR, DE",
                "2, 10, attempts, true, 22, DE, RU",
                "0, 10, attempts, true, 10, RU, UKR",
                "0, 10, attempts, true, 3, EN, UKR",
                "0,,,true, 26, EN, UKR",
                "0, 10,, true, 26, EN, UKR",
                ",,,, 26, EN, UKR",
                "0, 10, attempts, true, 0, EN, DE",
        })
        public void get_translations_status_200(
                Integer page,
                Integer size,
                String sortBy,
                Boolean isAsc,
                Integer totalElements,
                String sourceLanguage,
                String targetLanguage
        ) throws Exception {
            loginUser1();

            List<ResponseUserResultsTranslationDto> expectedResultList = getResponseUserResultsTranslationDtoList(
                    totalElements, sourceLanguage, targetLanguage);

            int defaultPage = 0;
            int defaultSize = 10;
            String defaultSortBy = "attempts";
            boolean defaultIsAsc = true;

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("sourceLanguage", sourceLanguage);
            params.add("targetLanguage", targetLanguage);
            if (page != null) params.add("page", String.valueOf(page));
            if (size != null) params.add("size", String.valueOf(size));
            if (sortBy != null) params.add("sortBy", sortBy);
            if (isAsc != null) params.add("isAsc", String.valueOf(isAsc));

            size = size == null ? defaultSize : size;
            page = page == null ? defaultPage : page;
            sortBy = sortBy == null ? defaultSortBy : sortBy;
            isAsc = isAsc == null ? defaultIsAsc : isAsc;

            int totalPage = size != 0 ? (int) Math.ceil((double) totalElements / size) : 0;
            boolean isLast = totalElements == 0 || page == totalPage - 1;
            boolean isFirst = page == 0;
            int contentSize = getContentSize(totalElements, size, isLast);

            MvcResult result = mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.pageNumber", is(page)))
                    .andExpect(jsonPath("$.pageSize", is(size)))
                    .andExpect(jsonPath("$.totalElements", is(totalElements)))
                    .andExpect(jsonPath("$.totalPages", is(totalPage)))
                    .andExpect(jsonPath("$.last", is(isLast)))
                    .andExpect(jsonPath("$.first", is(isFirst)))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(contentSize)))
                    .andReturn();
            String jsonResponse = result.getResponse().getContentAsString();

            PageResponseUserResultsTranslationDto responseDto = mapper.readValue(
                    jsonResponse, PageResponseUserResultsTranslationDto.class
            );

            responseDto.getContent().forEach(r -> {
                assertEquals(expectedResultList.stream().filter(e -> e.getLexemeId().equals(r.getLexemeId())).count(), 1);
            });
        }

        @ParameterizedTest(name = "Test {index}: Get translation with status 400 when languages are wrong[{arguments}]")
        @CsvSource({
                "TES, DE",
                "EN, 345a",
                "ksj&, &jjsTR"
        })
        public void get_translations_status_400_when_languages_are_wring(
                String sourceLanguage,
                String targetLanguage
        ) throws Exception {
            loginUser1();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (sourceLanguage != null) params.add("sourceLanguage", sourceLanguage);
            if (targetLanguage != null) params.add("targetLanguage", targetLanguage);

            mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @ParameterizedTest(name = "Test {index}: Get translation with status 400 when languages are null[{arguments}]")
        @CsvSource({
                ", DE",
                "TES,",
                ","
        })
        public void get_translations_status_400_when_languages_are_null(
                String sourceLanguage,
                String targetLanguage
        ) throws Exception {
            loginUser1();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (sourceLanguage != null) params.add("sourceLanguage", sourceLanguage);
            if (targetLanguage != null) params.add("targetLanguage", targetLanguage);

            mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest(name = "Test {index}: Get translation with status 400 when pagination parameter sortBy is wrong[{arguments}]")
        @CsvSource({
                " test",
                "skskskk ozii&8Y"
        })
        public void get_translations_status_400_when_pagination_parameter_sortBy_is_wrong(
                String sortBy
        ) throws Exception {
            loginUser1();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("sourceLanguage", "EN");
            params.add("targetLanguage", "DE");
            params.add("sortBy", sortBy);

            mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: Get translation with status 400 when pagination parameter are wrong[{arguments}]")
        @CsvSource({
                "-4, 10",
                "1, -10",
                "-4, -100",
                "0, 0",
        })
        public void get_translations_status_400_when_pagination_parameter_are_wrong(
                int page,
                int size
        ) throws Exception {
            loginUser1();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("sourceLanguage", "EN");
            params.add("targetLanguage", "DE");
            params.add("page", String.valueOf(page));
            params.add("size", String.valueOf(size));

            mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void get_translations_status_401_when_user_is_not_authorized() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("sourceLanguage", "EN");
            params.add("targetLanguage", "DE");

            mockMvc.perform(get(USER_LEXEMES_RESULT_TRANSLATION_URL)
                            .params(params)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }

    @Nested
    @DisplayName("PATCH " + USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
    public class UpdateUserLexemeResultStatusTests {

        @Test
        public void update_user_lexeme_result_status_status_204() throws Exception {
            loginUser1();

            int attempts = 6;
            int successfulAttempts = 3;
            User user = userRepository.findById(currentUserId1).get();

            List<Lexeme> createdLexemes = createNewLexemes(12);
            for (int i = 0; i < 10; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
            }
            List<UpdateStatusUserLexemeResultDto> updateDtos = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                UpdateStatusUserLexemeResultDto dto;
                if (i < 6) {
                    dto = UpdateStatusUserLexemeResultDto.builder()
                            .lexemeId(createdLexemes.get(i).getId())
                            .isActive(false)
                            .build();
                } else {
                    dto = UpdateStatusUserLexemeResultDto.builder()
                            .lexemeId(createdLexemes.get(i).getId())
                            .isActive(true)
                            .build();
                }
                updateDtos.add(dto);
            }
            String jsonDto = mapper.writeValueAsString(updateDtos);

            mockMvc.perform(patch(USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonDto))
                    .andExpect(status().isNoContent());

            List<UserLexemeResult> list = userLexemeResultRepository.findAll();
            long countOfActive = list.stream().filter(UserLexemeResult::getIsActive).count();

            assertEquals(countOfActive, 4L);
        }

        @Test
        public void update_user_lexeme_result_status_status_400_when_dto_is_null() throws Exception {
            loginUser1();
            mockMvc.perform(patch(USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void update_user_lexeme_result_status_status_400_when_dto_is_empty() throws Exception {
            loginUser1();
            List<UpdateStatusUserLexemeResultDto> updateDtos = new ArrayList<>();
            String jsonDto = mapper.writeValueAsString(updateDtos);
            mockMvc.perform(patch(USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1)
                            .content(jsonDto)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @ParameterizedTest
        @CsvSource({
                ",",
                ",false",
                "8bdbca11-ac4a-4b25-ab3c-a4e0e31340af, ",
        })
        public void update_user_lexeme_result_status_status_400_when_dto_data_is_wrong(
                UUID lexemeId,
                Boolean isActive
        ) throws Exception {

            loginUser1();
            UpdateStatusUserLexemeResultDto wrongDto = UpdateStatusUserLexemeResultDto.builder()
                    .lexemeId(lexemeId)
                    .isActive(isActive)
                    .build();
            List<UpdateStatusUserLexemeResultDto> updateDtos = new ArrayList<>();
            updateDtos.add(wrongDto);

            int attempts = 6;
            int successfulAttempts = 3;
            User user = userRepository.findById(currentUserId1).get();

            List<Lexeme> createdLexemes = createNewLexemes(12);
            for (int i = 0; i < 10; i++) {
                createNewUserLexemeResults(user,
                        createdLexemes.get(i),
                        attempts,
                        successfulAttempts);
            }

            for (int i = 0; i < 10; i++) {
                UpdateStatusUserLexemeResultDto dto;
                if (i < 6) {
                    dto = UpdateStatusUserLexemeResultDto.builder()
                            .lexemeId(createdLexemes.get(i).getId())
                            .isActive(false)
                            .build();
                } else {
                    dto = UpdateStatusUserLexemeResultDto.builder()
                            .lexemeId(createdLexemes.get(i).getId())
                            .isActive(true)
                            .build();
                }
                updateDtos.add(dto);
            }
            String jsonDto = mapper.writeValueAsString(updateDtos);


            mockMvc.perform(patch(USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1)
                            .content(jsonDto)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());

            List<UserLexemeResult> list = userLexemeResultRepository.findAll();
            long countOfActive = list.stream().filter(UserLexemeResult::getIsActive).count();
            assertEquals(countOfActive, 10L);
        }

        @Test
        public void update_user_lexeme_result_status_status_401_when_user_unauthorized() throws Exception {

            mockMvc.perform(patch(USER_LEXEMES_RESULT_UPDATE_STATUS_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test Token")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }
}

