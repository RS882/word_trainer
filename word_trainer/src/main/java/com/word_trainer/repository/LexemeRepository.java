package com.word_trainer.repository;

import com.word_trainer.domain.entity.Lexeme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LexemeRepository extends JpaRepository<Lexeme, UUID> {
}
