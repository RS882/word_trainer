package com.word_trainer.schedulers;

import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.security.domain.entity.RefreshToken;
import com.word_trainer.security.domain.entity.TokenBlackList;
import com.word_trainer.security.repositorys.TokenBlackListRepository;
import com.word_trainer.security.repositorys.TokenRepository;
import com.word_trainer.services.mapping.UserMapperService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Scheduler clean expired tokens integration tests ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class ExpiredTokensCleanerTest {

    @Autowired
    private ExpiredTokensCleaner expiredTokensCleaner;

    @Nested
    @DisplayName("Remove token from token black list when token is expired")
    class CleanTokenBlackListTests {

        @Autowired
        private TokenBlackListRepository tokenBlackListRepository;

        @Test
        public void clean_token_black_list_positive_test() throws InterruptedException {

            TokenBlackList token = TokenBlackList.builder()
                    .token("test-token")
                    .deleteAfterDatetime(LocalDateTime.now().plusSeconds(1))
                    .build();

            TokenBlackList token1 = TokenBlackList.builder()
                    .token("test-token1")
                    .deleteAfterDatetime(LocalDateTime.now().plusSeconds(1))
                    .build();
            TokenBlackList token3 = TokenBlackList.builder()
                    .token("test-token3")
                    .deleteAfterDatetime(LocalDateTime.now().plusSeconds(10))
                    .build();

            List<TokenBlackList> tokenBlackLists = new ArrayList<>();
            tokenBlackLists.add(token);
            tokenBlackLists.add(token1);
            tokenBlackLists.add(token3);

            tokenBlackListRepository.saveAll(tokenBlackLists);

            Thread.sleep(3000);

            expiredTokensCleaner.cleanTokenBlackList();

            List<TokenBlackList> remainingTokens = tokenBlackListRepository.findAll();
            assertEquals(1, remainingTokens.size());
            assertEquals("test-token3", remainingTokens.get(0).getToken());
        }
    }

    @Nested
    @DisplayName("Remove refresh token from refresh token  when token is expired")
    class cleanRefreshTokenTests {

        @Autowired
        private TokenRepository tokenRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserMapperService mapperService;

        private static final String TEST_USER_NAME_1 = "TestName1";
        private static final String TEST_USER_EMAIL_1 = "test.user1@test.com";
        private static final String TEST_USER_PASSWORD_1 = "qwerty!123";

        @Test
        public void clean_refresh_token_positive_test() throws InterruptedException {

            UserRegistrationDto dto = UserRegistrationDto
                    .builder()
                    .email(TEST_USER_EMAIL_1)
                    .userName(TEST_USER_NAME_1)
                    .password(TEST_USER_PASSWORD_1)
                    .build();
           User savedUser = userRepository.save(mapperService.toEntity(dto));

            RefreshToken token = RefreshToken.builder()
                    .token("test-token")
                    .user(savedUser)
                    .expireAt(LocalDateTime.now().plusSeconds(1))
                    .build();

            RefreshToken token1 = RefreshToken.builder()
                    .token("test-token1")
                    .user(savedUser)
                    .expireAt(LocalDateTime.now().plusSeconds(1))
                    .build();
            RefreshToken token3 = RefreshToken.builder()
                    .token("test-token3")
                    .user(savedUser)
                    .expireAt(LocalDateTime.now().plusSeconds(10))
                    .build();

            List<RefreshToken> refreshTokenList= new ArrayList<>();
            refreshTokenList.add(token);
            refreshTokenList.add(token1);
            refreshTokenList.add(token3);

            tokenRepository.saveAll(refreshTokenList);

            Thread.sleep(3000);

            expiredTokensCleaner.cleanRefreshToken();

            List<RefreshToken> remainingTokens = tokenRepository.findAll();
            assertEquals(1, remainingTokens.size());
            assertEquals("test-token3", remainingTokens.get(0).getToken());
        }
    }
}