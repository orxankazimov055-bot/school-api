package az.iktex.organization.structure.testders.security.jwt;

import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(props.secret())
        );
    }

    public IssuedToken generate(
            Account account,
            TokenType type,
            String sessionId
    ) {

        Instant now = Instant.now();

        Duration ttl = type == TokenType.ACCESS
                ? props.accessTokenTtl()
                : props.refreshTokenTtl();

        Instant exp = now.plus(ttl);

        String jti = UUID.randomUUID().toString();

        JwtBuilder builder = Jwts.builder()
                .issuer(props.issuer())
                .subject(account.getUsername())
                .id(jti)
                .claim("sid", sessionId)
                .claim("type", type.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp));

        if (type == TokenType.ACCESS) {
            builder.claim("role", account.getRole().name());
        }

        String value = builder.signWith(key).compact();

        return new IssuedToken(
                value,
                jti,
                type,
                now,
                exp
        );
    }

    public Claims parse(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}