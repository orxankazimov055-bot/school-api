package az.iktex.organization.structure.testders.entity;

import az.iktex.organization.structure.testders.enums.RevokeReason;
import az.iktex.organization.structure.testders.enums.TokenType;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "auth_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_auth_token_jti",
                        columnNames = "jti"
                )
        },
        indexes = {
                @Index(
                        name = "idx_auth_token_session",
                        columnList = "session_id"
                ),
                @Index(
                        name = "idx_auth_token_account_revoked",
                        columnList = "account_id, revoked"
                )
        }
)
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String jti;

    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 10)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_auth_token_account"
            )
    )
    private Account account;

    @Column(name = "session_started_at")
    private Instant sessionStartedAt;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoke_reason", length = 30)
    private RevokeReason revokeReason;
}