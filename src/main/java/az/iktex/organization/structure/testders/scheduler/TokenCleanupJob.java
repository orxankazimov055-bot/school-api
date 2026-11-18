package az.iktex.organization.structure.testders.scheduler;

import az.iktex.organization.structure.testders.repository.AuthTokenRepository;
import az.iktex.organization.structure.testders.repository.TokenBlacklistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupJob {

    private final TokenBlacklistRepository blacklistRepository;
    private final AuthTokenRepository authTokenRepository;

    @Transactional
    @Scheduled(cron = "${app.security.token-cleanup-cron}")
    public void cleanup() {

        MDC.put("traceId", "cleanup-" + UUID.randomUUID());

        try {

            Instant now = Instant.now();

            int blacklist = blacklistRepository.deleteExpired(now);

            int tokens = authTokenRepository.deleteExpired(now);

            log.info(
                    "Token cleanup: token_blacklist={}, auth_tokens={} rows deleted",
                    blacklist,
                    tokens
            );

        } finally {
            MDC.remove("traceId");
        }
    }
}