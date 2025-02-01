package com.word_trainer.schedulers;

import com.word_trainer.security.repositorys.TokenBlackListRepository;
import com.word_trainer.security.repositorys.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ExpiredTokensCleaner {

    private final TokenBlackListRepository tokenBlackListRepository;

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanTokenBlackList() {
        tokenBlackListRepository.deleteExpiredTokens();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanRefreshToken() {
        tokenRepository.deleteExpiredTokens();
    }
}
