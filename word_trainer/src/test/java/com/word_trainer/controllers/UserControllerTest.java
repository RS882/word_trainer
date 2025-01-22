package com.word_trainer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.repository.UserLexemeResultRepository;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.services.interfaces.LexemeService;
import com.word_trainer.services.mapping.UserMapperService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("User controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapperService mapperService;

    private ObjectMapper mapper = new ObjectMapper();

    private static final String LOGIN_URL = "/v1/auth/login";
    private final String USER_REGISTRATION_PATH = "/v1/user/registration";
    private final String USER_ME_PATH = "/v1/user/me";

    private String accessToken1;
    private Long currentUserId1;

    private static final String TEST_USER_NAME_1 = "TestName1";
    private static final String TEST_USER_NAME_2 = "TestName2";
    private static final String TEST_USER_EMAIL_1 = "test.user1@test.com";
    private static final String TEST_USER_PASSWORD_1 = "qwerty!123";
    private static final String TEST_USER_PASSWORD_2 = "jwerty!123";

    private void loginUser1() throws Exception {
        TokenResponseDto responseDto = loginUser(TEST_USER_EMAIL_1, TEST_USER_NAME_1, TEST_USER_PASSWORD_1);
        accessToken1 = responseDto.getAccessToken();
        currentUserId1 = responseDto.getUserId();
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

    @Nested
    @DisplayName("POST /v1/user/registration")
    class RegistrationUserTest {

        @Test
        public void createUser_with_status_201() throws Exception {
            String dtoJson = mapper.writeValueAsString(UserRegistrationDto.builder()
                    .email(TEST_USER_EMAIL_1)
                    .userName(TEST_USER_NAME_1)
                    .password(TEST_USER_PASSWORD_1)
                    .build());
            mockMvc.perform(post(USER_REGISTRATION_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userName", is(TEST_USER_NAME_1)))
                    .andExpect(jsonPath("$.email", is(TEST_USER_EMAIL_1)))
                    .andExpect(jsonPath("$.userId", isA(Number.class)))
                    .andExpect(jsonPath("$.userId", greaterThanOrEqualTo(0)))
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles[0]", is("ROLE_USER")));
        }

        @Test
        public void createUser_with_status_400_email_address_already_in_use() throws Exception {
            String dtoJson = mapper.writeValueAsString(UserRegistrationDto.builder()
                    .email(TEST_USER_EMAIL_1)
                    .userName(TEST_USER_NAME_1)
                    .password(TEST_USER_PASSWORD_1)
                    .build());

            mockMvc.perform(post(USER_REGISTRATION_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isCreated());

            String dtoJson2 = mapper.writeValueAsString(UserRegistrationDto.builder()
                    .email(TEST_USER_EMAIL_1)
                    .userName(TEST_USER_NAME_2)
                    .password(TEST_USER_PASSWORD_2)
                    .build());

            mockMvc.perform(post(USER_REGISTRATION_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson2))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @ParameterizedTest(name = "Test {index}: createUser_with_status_400_registration_data_is_incorrect [{arguments}]")
        @MethodSource("incorrectRegistrationData")
        public void createUser_with_status_400_registration_data_is_incorrect(UserRegistrationDto dto) throws Exception {
            String dtoJson2 = mapper.writeValueAsString(dto);

            mockMvc.perform(post(USER_REGISTRATION_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson2))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        private static Stream<Arguments> incorrectRegistrationData() {
            return Stream.of(Arguments.of(
                            UserRegistrationDto.builder()
                                    .email("testexample?com")
                                    .userName(TEST_USER_NAME_1)
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email("testexample23om")
                                    .userName(TEST_USER_NAME_1)
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .userName(TEST_USER_NAME_1)
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName("uI")
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName("uIasdsdasfsdf_Asid2032o4p12o3joefhsodfhosdhf898ihoih2434efg34grfgdfgsdfasdposapfjsddfhg")
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .password(TEST_USER_PASSWORD_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("1E")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("asdasdlDFsd90q!u023402lks@djalsdajsd#lahsdkahs$$%dllkasd")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("asdasdlweqwe")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("asda@sdlweqwe")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("asdasdlwe8qwe")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .password("Qsdasdlwe8qwe")
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email(TEST_USER_EMAIL_1)
                                    .userName(TEST_USER_NAME_1)
                                    .build()),
                    Arguments.of(
                            UserRegistrationDto.builder()
                                    .email("testexample?com")
                                    .userName("2y")
                                    .password("Qsdasdlwe8qwe")
                                    .build())
            );
        }
    }

    @Nested
    @DisplayName("GET /v1/user/me")
    class GetMeInfoTest {

        @Test
        public void getMeInformation_with_status_200() throws Exception {
            loginUser1();

            mockMvc.perform(get(USER_ME_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userName", is(TEST_USER_NAME_1)))
                    .andExpect(jsonPath("$.email", is(TEST_USER_EMAIL_1)))
                    .andExpect(jsonPath("$.userId", isA(Number.class)))
                    .andExpect(jsonPath("$.userId", greaterThanOrEqualTo(0)))
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles[0]", is("ROLE_USER")));
        }

        @Test
        public void getMeInformation_with_status_401_user_is_not_authorized() throws Exception {

            mockMvc.perform(get(USER_ME_PATH)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "test token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

        }
    }
}