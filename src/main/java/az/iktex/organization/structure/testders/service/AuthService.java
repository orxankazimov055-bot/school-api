package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.RegisterRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;

import az.iktex.organization.structure.testders.dto.request.LoginRequest;
import az.iktex.organization.structure.testders.dto.request.RefreshRequest;
import az.iktex.organization.structure.testders.dto.request.ChangePasswordRequest;
import az.iktex.organization.structure.testders.dto.response.AuthResponse;
import az.iktex.organization.structure.testders.dto.response.SessionResponse;

import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.entity.AuthToken;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.enums.TokenType;
import az.iktex.organization.structure.testders.enums.RevokeReason;

import az.iktex.organization.structure.testders.exception.BadRequestException;
import az.iktex.organization.structure.testders.exception.ConflictException;
import az.iktex.organization.structure.testders.exception.ForbiddenException;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.exception.UnauthorizedException;

import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.repository.AuthTokenRepository;
import az.iktex.organization.structure.testders.repository.TokenBlacklistRepository;

import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.security.jwt.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final AuthTokenRepository authTokenRepository;
    private final TokenBlacklistRepository blacklistRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;

    private final EntityManager entityManager;

    // ================= REGISTER =================

    @Transactional
    public AccountResponse register(RegisterRequest request) {

        if (accountRepository.existsByUsername(request.username())) {
            throw new ConflictException(
                    ErrorCode.ACCOUNT_USERNAME_EXISTS
            );
        }

        if (accountRepository.existsByEmail(request.email())) {
            throw new ConflictException(
                    ErrorCode.ACCOUNT_EMAIL_EXISTS
            );
        }

        Account account = Account.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();

        account = accountRepository.save(account);

        log.info(
                "Account registered. AccountId: {}",
                account.getId()
        );

        return toResponse(account);
    }

    // ================= LOGIN =================

    @Transactional
    public AuthResponse login(LoginRequest request) {

        // Hesab username ve ya email ile tapilir
        var optionalAccount = accountRepository
                .findByUsernameOrEmail(
                        request.login(),
                        request.login()
                );

        if (optionalAccount.isEmpty()) {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.login(),
                            request.password()
                    )
            );

            throw new UnauthorizedException(
                    ErrorCode.AUTH_BAD_CREDENTIALS
            );
        }

        Account account = optionalAccount.get();

        // Hesab kilidlenir ve bazadan yeniden oxunur
        entityManager.refresh(
                account,
                LockModeType.PESSIMISTIC_WRITE
        );

        // Kilid alindiqdan sonra autentifikasiya
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                )
        );

        if (!account.isEnabled()) {
            throw new ForbiddenException(
                    ErrorCode.AUTH_ACCOUNT_DISABLED
            );
        }

        String sessionId = UUID.randomUUID().toString();

        log.info(
                "Login successful. AccountId: {}",
                account.getId()
        );

        return tokenService.issuePair(account, sessionId);
    }

    // ================= REFRESH =================

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public AuthResponse refresh(RefreshRequest request) {

        Claims claims;

        try {

            claims = jwtService.parse(request.refreshToken());

        } catch (ExpiredJwtException ex) {

            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_EXPIRED
            );

        } catch (JwtException | IllegalArgumentException ex) {

            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_INVALID
            );
        }

        String type = claims.get("type", String.class);

        if (!TokenType.REFRESH.name().equals(type)) {
            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_TYPE_MISMATCH
            );
        }

        String jti = claims.getId();
        String sessionId = claims.get("sid", String.class);
        String username = claims.getSubject();

        if (jti == null || jti.isBlank()
                || sessionId == null || sessionId.isBlank()
                || username == null || username.isBlank()) {

            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_INVALID
            );
        }

        // Hesab kilidlenir
        Account account = accountRepository
                .findByUsernameForUpdate(username)
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.AUTH_TOKEN_INVALID
                ));

        // Refresh reuse detection
        var blacklist = blacklistRepository.findByJti(jti);

        if (blacklist.isPresent()) {

            if (blacklist.get().getReason()
                    == RevokeReason.REFRESH_ROTATION) {

                blacklistService.revokeAll(
                        account.getId(),
                        RevokeReason.REFRESH_REUSE_DETECTED
                );

                log.warn(
                        "Refresh token reuse detected. AccountId: {}",
                        account.getId()
                );

                throw new UnauthorizedException(
                        ErrorCode.AUTH_REFRESH_REUSED
                );
            }

            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_REVOKED
            );
        }

        if (!account.isEnabled()) {
            throw new ForbiddenException(
                    ErrorCode.AUTH_ACCOUNT_DISABLED
            );
        }

        AuthToken oldToken = authTokenRepository
                .findByJti(jti)
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.AUTH_TOKEN_INVALID
                ));

        if (!oldToken.getAccount().getId().equals(account.getId())
                || !oldToken.getSessionId().equals(sessionId)
                || oldToken.getTokenType() != TokenType.REFRESH) {

            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_INVALID
            );
        }

        if (oldToken.isRevoked()) {
            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_REVOKED
            );
        }

        if (!oldToken.getExpiresAt().isAfter(Instant.now())) {
            throw new UnauthorizedException(
                    ErrorCode.AUTH_TOKEN_EXPIRED
            );
        }

        // Sessiyanin ilkin tarixi saxlanilir
        Instant sessionStartedAt = resolveSessionStartedAt(oldToken);

        // Kohne access ve refresh tokenleri legv edilir
        blacklistService.revokeSession(
                sessionId,
                RevokeReason.REFRESH_ROTATION
        );

        // Eyni sessionId ve ilkin login tarixi
        return tokenService.issuePair(
                account,
                sessionId,
                sessionStartedAt
        );
    }

    // ================= LOGOUT =================

    @Transactional
    public void logout(AuthPrincipal principal) {

        findAccountForUpdate(principal.accountId());

        blacklistService.revokeSession(
                principal.sid(),
                RevokeReason.LOGOUT
        );
    }

    // ================= LOGOUT ALL =================

    @Transactional
    public void logoutAll(AuthPrincipal principal) {

        findAccountForUpdate(principal.accountId());

        blacklistService.revokeAll(
                principal.accountId(),
                RevokeReason.LOGOUT_ALL
        );
    }

    // ================= ME =================

    @Transactional(readOnly = true)
    public AccountResponse me(AuthPrincipal principal) {

        return toResponse(
                findAccount(principal.accountId())
        );
    }

    // ================= SESSIONS =================

    @Transactional(readOnly = true)
    public List<SessionResponse> sessions(AuthPrincipal principal) {

        List<AuthToken> tokens = authTokenRepository.findActive(
                principal.accountId(),
                TokenType.REFRESH,
                Instant.now()
        );

        return tokens.stream()
                .map(token -> new SessionResponse(
                        token.getSessionId(),
                        resolveSessionStartedAt(token),
                        token.getExpiresAt(),
                        token.getSessionId().equals(principal.sid())
                ))
                .toList();
    }

    // ================= REVOKE SESSION =================

    @Transactional
    public void revokeSession(
            AuthPrincipal principal,
            String sessionId
    ) {

        findAccountForUpdate(principal.accountId());

        boolean exists = authTokenRepository.findActive(
                        principal.accountId(),
                        TokenType.REFRESH,
                        Instant.now()
                )
                .stream()
                .anyMatch(token ->
                        token.getSessionId().equals(sessionId)
                );

        if (!exists) {
            throw new NotFoundException(
                    ErrorCode.SESSION_NOT_FOUND
            );
        }

        blacklistService.revokeSession(
                sessionId,
                RevokeReason.SESSION_REVOKED
        );
    }

    // ================= CHANGE PASSWORD =================

    @Transactional
    public void changePassword(
            AuthPrincipal principal,
            ChangePasswordRequest request
    ) {

        Account account =
                findAccountForUpdate(principal.accountId());

        if (!passwordEncoder.matches(
                request.oldPassword(),
                account.getPassword()
        )) {

            throw new BadRequestException(
                    ErrorCode.AUTH_OLD_PASSWORD_WRONG
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                account.getPassword()
        )) {

            throw new BadRequestException(
                    ErrorCode.AUTH_PASSWORD_SAME
            );
        }

        account.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        account.setPasswordChangedAt(Instant.now());

        accountRepository.save(account);

        blacklistService.revokeAll(
                account.getId(),
                RevokeReason.PASSWORD_CHANGED
        );

        log.info(
                "Password changed. AccountId: {}",
                account.getId()
        );
    }

    // ================= SESSION STARTED AT =================

    private Instant resolveSessionStartedAt(AuthToken token) {

        if (token.getSessionStartedAt() != null) {

            return token.getSessionStartedAt();
        }

        return token.getIssuedAt();
    }

    // ================= FIND ACCOUNT =================

    private Account findAccount(Long accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    // ================= FIND ACCOUNT WITH LOCK =================

    private Account findAccountForUpdate(Long accountId) {

        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    // ================= RESPONSE =================

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                account.isEnabled(),
                account.getCreatedAt()
        );
    }
}