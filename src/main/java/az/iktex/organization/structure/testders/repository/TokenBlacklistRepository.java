package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.TokenBlacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByJti(String jti);

    Optional<TokenBlacklist> findByJti(String jti);

    long countByAccountId(Long accountId);

    // ================= DELETE EXPIRED =================

    @Modifying
    @Query("""
            delete from TokenBlacklist b
            where b.expiresAt < :now
            """)
    int deleteExpired(@Param("now") Instant now);

    // ================= SAFE BLACKLIST INSERT =================

    @Modifying
    @Query(value = """
            INSERT INTO token_blacklist (
                jti,
                account_id,
                token_type,
                reason,
                expires_at,
                blacklisted_at
            )
            VALUES (
                :jti,
                :accountId,
                :tokenType,
                :reason,
                :expiresAt,
                :blacklistedAt
            )
            ON CONFLICT (jti) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("jti") String jti,
            @Param("accountId") Long accountId,
            @Param("tokenType") String tokenType,
            @Param("reason") String reason,
            @Param("expiresAt") Instant expiresAt,
            @Param("blacklistedAt") Instant blacklistedAt
    );
}