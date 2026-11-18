package az.iktex.organization.structure.testders.entity;

import az.iktex.organization.structure.testders.enums.RevokeReason;
import az.iktex.organization.structure.testders.enums.TokenType;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "token_blacklist",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blacklist_jti",
                        columnNames = "jti"
                )
        },
        indexes = {
                @Index(
                        name = "idx_blacklist_expires_at",
                        columnList = "expires_at"
                )
        }
)
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String jti;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                    name = "fk_blacklist_account"
            )
    )
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 10)
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RevokeReason reason;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(
            name = "blacklisted_at",
            nullable = false,
            updatable = false
    )
    private Instant blacklistedAt;
}