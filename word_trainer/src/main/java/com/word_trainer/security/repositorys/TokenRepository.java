package com.word_trainer.security.repositorys;


import com.word_trainer.security.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<List<RefreshToken>> findByUserId(Long id);

    void deleteAllByToken(String token);
}
