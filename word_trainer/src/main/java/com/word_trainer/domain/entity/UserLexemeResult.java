package com.word_trainer.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.word_trainer.constants.language.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_lexeme_result")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class UserLexemeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "successful_attempts")
    private int successfulAttempts;

    @Column(name = "source_language")
    @Enumerated(EnumType.STRING)
    private Language sourceLanguage;

    @Column(name = "target_language")
    @Enumerated(EnumType.STRING)
    private Language targetLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
