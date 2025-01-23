package com.word_trainer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word_trainer.constants.LexemeType;
import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private ObjectMapper mapper = new ObjectMapper();

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
    private static final String USER_LEXEMES_RESULT_URL = "/v1/user/lexeme/result";

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
    @DisplayName("POST /v1/user/lexeme/result")
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
}