package com.word_trainer.security.repositorys;

import com.word_trainer.security.domain.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenBlackList t WHERE t.deleteAfterDatetime < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
