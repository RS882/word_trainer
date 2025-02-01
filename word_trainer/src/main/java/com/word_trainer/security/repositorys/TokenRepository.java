package com.word_trainer.security.repositorys;


import com.word_trainer.security.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<List<RefreshToken>> findByUserId(Long id);

    void deleteAllByToken(String token);

    void deleteByUserId(Long id);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expireAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
