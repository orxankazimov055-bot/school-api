package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.AuthToken;
import az.iktex.organization.structure.testders.enums.RevokeReason;
import az.iktex.organization.structure.testders.enums.ErrorCode;

import az.iktex.organization.structure.testders.exception.NotFoundException;

import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.repository.AuthTokenRepository;
import az.iktex.organization.structure.testders.repository.TokenBlacklistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final AuthTokenRepository authTokenRepository;
    private final TokenBlacklistRepository blacklistRepository;
    private final AccountRepository accountRepository;

    // ================= CHECK BLACKLIST =================

    @Transactional(readOnly = true)
    public boolean isBlacklisted(String jti) {

        return blacklistRepository.existsByJti(jti);
    }

    // ================= BLACKLIST TOKEN =================

    @Transactional
    public void blacklist(AuthToken token, RevokeReason reason) {

        lockAccount(token.getAccount().getId());

        AuthToken currentToken = authTokenRepository
                .findByJti(token.getJti())
                .orElse(null);

        if (currentToken == null) {
            return;
        }

        blacklistToken(currentToken, reason);
    }

    // ================= INTERNAL BLACKLIST =================

    private void blacklistToken(
            AuthToken token,
            RevokeReason reason
    ) {

        Instant now = Instant.now();

        if (token.isRevoked()) {
            return;
        }

        token.setRevoked(true);
        token.setRevokedAt(now);
        token.setRevokeReason(reason);

        authTokenRepository.save(token);

        // Muddeti bitmis token blacklist-e yazilmir
        if (!token.getExpiresAt().isAfter(now)) {
            return;
        }

        int inserted = blacklistRepository.insertIfAbsent(
                token.getJti(),
                token.getAccount().getId(),
                token.getTokenType().name(),
                reason.name(),
                token.getExpiresAt(),
                now
        );

        if (inserted == 0) {

            log.debug("Token already blacklisted");

        } else {

            log.debug(
                    "Token revoked. Reason: {}",
                    reason
            );
        }
    }

    // ================= REVOKE SESSION =================

    @Transactional
    public void revokeSession(
            String sessionId,
            RevokeReason reason
    ) {

        List<AuthToken> tokens = authTokenRepository
                .findAllBySessionIdAndRevokedFalse(sessionId);

        for (AuthToken token : tokens) {

            blacklistToken(token, reason);
        }

        log.info(
                "Session revoked. Reason: {}, Token count: {}",
                reason,
                tokens.size()
        );
    }

    // ================= REVOKE ALL =================

    @Transactional
    public void revokeAll(
            Long accountId,
            RevokeReason reason
    ) {

        lockAccount(accountId);

        List<AuthToken> tokens = authTokenRepository
                .findAllByAccountIdAndRevokedFalse(accountId);

        for (AuthToken token : tokens) {

            blacklistToken(token, reason);
        }

        log.info(
                "Account tokens revoked. AccountId: {}, Reason: {}, Count: {}",
                accountId,
                reason,
                tokens.size()
        );
    }

    // ================= ACCOUNT LOCK =================

    private void lockAccount(Long accountId) {

        accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND
                ));
    }
}