package com.word_trainer.security.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Entity
@Table(name = "token-black-list")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlackList {

    @Transient
    @Value("${expires.access}")
    private int expiresAccessInMinutes;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "token", length = 512)
    private String token;

    @Column(name = "delete_after_datetime", nullable = false, updatable = false)
    private LocalDateTime deleteAfterDatetime;

    @PrePersist
    protected void onCreate() {
        deleteAfterDatetime = LocalDateTime.now().plusMinutes(expiresAccessInMinutes);
    }
}
