package com.word_trainer.domain.entity;

import com.word_trainer.audit.EntityAudit;
import com.word_trainer.constants.language.Language;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "translation",
        indexes = @Index(name = "idx_meaning", columnList = "meaning"))
@Data
@EqualsAndHashCode(callSuper = true, exclude = "lexeme")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Translation extends EntityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;
}
