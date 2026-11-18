package az.iktex.organization.structure.testders.security.jwt;

import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.enums.TokenType;
import az.iktex.organization.structure.testders.exception.BaseException;
import az.iktex.organization.structure.testders.exception.ForbiddenException;
import az.iktex.organization.structure.testders.exception.UnauthorizedException;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.service.TokenBlacklistService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final AccountRepository accountRepository;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            TokenBlacklistService blacklistService,
            AccountRepository accountRepository,
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver resolver
    ) {
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
        this.accountRepository = accountRepository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        if (header == null || !header.startsWith(BEARER)) {
            chain.doFilter(request, response);
            return;
        }

        try {

            String token = header.substring(BEARER.length());

            Claims claims = jwtService.parse(token);

            String type = claims.get("type", String.class);

            // Yalniz ACCESS token qebul olunur
            if (!TokenType.ACCESS.name().equals(type)) {
                throw new UnauthorizedException(
                        ErrorCode.AUTH_TOKEN_TYPE_MISMATCH
                );
            }

            String jti = claims.getId();
            String sid = claims.get("sid", String.class);
            String username = claims.getSubject();

            // Token melumatlarinin yoxlanilmasi
            if (jti == null || jti.isBlank()
                    || sid == null || sid.isBlank()
                    || username == null || username.isBlank()) {

                throw new UnauthorizedException(
                        ErrorCode.AUTH_TOKEN_INVALID
                );
            }

            // Blacklist yoxlamasi
            if (blacklistService.isBlacklisted(jti)) {
                throw new UnauthorizedException(
                        ErrorCode.AUTH_TOKEN_REVOKED
                );
            }

            // Hesabin bazadan tapilmasi
            Account account = accountRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new ForbiddenException(
                            ErrorCode.AUTH_ACCOUNT_DISABLED
                    ));

            // Hesab aktivdirmi?
            if (!account.isEnabled()) {
                throw new ForbiddenException(
                        ErrorCode.AUTH_ACCOUNT_DISABLED
                );
            }

            // Istifadeci melumatlari
            AuthPrincipal principal = new AuthPrincipal(
                    account.getId(),
                    account.getUsername(),
                    account.getRole(),
                    jti,
                    sid
            );

            // Rol token-den deyil, bazadan goturulur
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + account.getRole().name()
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(authority)
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            MDC.put("username", account.getUsername());

        } catch (ExpiredJwtException ex) {

            fail(
                    request,
                    response,
                    new UnauthorizedException(
                            ErrorCode.AUTH_TOKEN_EXPIRED
                    )
            );

            return;

        } catch (JwtException | IllegalArgumentException ex) {

            fail(
                    request,
                    response,
                    new UnauthorizedException(
                            ErrorCode.AUTH_TOKEN_INVALID
                    )
            );

            return;

        } catch (BaseException ex) {

            fail(request, response, ex);
            return;
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("username");
        }
    }

    private void fail(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception ex
    ) {

        SecurityContextHolder.clearContext();

        resolver.resolveException(
                request,
                response,
                null,
                ex
        );
    }
}