package com.word_trainer.domain.entity;

import com.word_trainer.audit.EntityAudit;
import com.word_trainer.constants.LexemeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "lexeme")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "translations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Lexeme extends EntityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private LexemeType type;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "lexeme", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Translation> translations = new HashSet<>();
}
