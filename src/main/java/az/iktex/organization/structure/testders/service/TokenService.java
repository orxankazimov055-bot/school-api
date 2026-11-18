package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.dto.response.AuthResponse;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.entity.AuthToken;
import az.iktex.organization.structure.testders.enums.TokenType;
import az.iktex.organization.structure.testders.repository.AuthTokenRepository;
import az.iktex.organization.structure.testders.security.jwt.IssuedToken;
import az.iktex.organization.structure.testders.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthTokenRepository authTokenRepository;

    // ================= NEW SESSION =================

    @Transactional
    public AuthResponse issuePair(
            Account account,
            String sessionId
    ) {

        return issuePair(
                account,
                sessionId,
                Instant.now()
        );
    }

    // ================= ISSUE TOKEN PAIR =================

    @Transactional
    public AuthResponse issuePair(
            Account account,
            String sessionId,
            Instant sessionStartedAt
    ) {

        // Access token yaradilir
        IssuedToken accessToken = jwtService.generate(
                account,
                TokenType.ACCESS,
                sessionId
        );

        // Refresh token yaradilir
        IssuedToken refreshToken = jwtService.generate(
                account,
                TokenType.REFRESH,
                sessionId
        );

        // Her iki token bazaya yazilir
        authTokenRepository.saveAll(
                List.of(
                        toEntity(
                                account,
                                sessionId,
                                sessionStartedAt,
                                accessToken
                        ),
                        toEntity(
                                account,
                                sessionId,
                                sessionStartedAt,
                                refreshToken
                        )
                )
        );

        log.info(
                "Token pair issued. AccountId: {}",
                account.getId()
        );

        return new AuthResponse(
                "Bearer",
                accessToken.value(),
                expiresIn(accessToken),
                refreshToken.value(),
                expiresIn(refreshToken)
        );
    }

    // ================= TOKEN ENTITY =================

    private AuthToken toEntity(
            Account account,
            String sessionId,
            Instant sessionStartedAt,
            IssuedToken token
    ) {

        return AuthToken.builder()
                .jti(token.jti())
                .sessionId(sessionId)
                .sessionStartedAt(sessionStartedAt)
                .tokenType(token.type())
                .account(account)
                .issuedAt(token.issuedAt())
                .expiresAt(token.expiresAt())
                .revoked(false)
                .build();
    }

    // ================= EXPIRES IN =================

    private long expiresIn(IssuedToken token) {

        return Duration.between(
                token.issuedAt(),
                token.expiresAt()
        ).toSeconds();
    }
}