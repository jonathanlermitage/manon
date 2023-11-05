package manon.service.app.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import manon.app.Cfg;
import manon.service.app.AuthTokenService;
import manon.service.app.JwtTokenService;
import manon.util.Tools;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String FIELD_TOKEN_ID = "tokenId";

    private final Cfg cfg;
    private final String jwtIssuer;
    private final SecretKey jwtSigningKey;
    private final JwtParser jwtParser;
    private final AuthTokenService authTokenService;

    public JwtTokenServiceImpl(@NotNull Cfg cfg, AuthTokenService authTokenService) {
        this.cfg = cfg;
        this.jwtIssuer = cfg.getSecurityJwtIssuer();
        this.jwtSigningKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(cfg.getSecurityJwtSigningKeyB64()));
        this.jwtParser = Jwts.parser().verifyWith(jwtSigningKey).build();
        this.authTokenService = authTokenService;
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public long getAuthTokenIdFromToken(String token) {
        return getClaimFromToken(token, claims -> Long.parseLong(claims.get(FIELD_TOKEN_ID).toString()));
    }

    @Override
    public <T> T getClaimFromToken(String token, @NotNull Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return jwtParser
            .parseSignedClaims(token)
            .getPayload();
    }

    @Override
    public String generateToken(String username) {
        ZonedDateTime expirationDate = Tools.now().plus(cfg.getSecurityJwtTokenTtl()).atZone(Tools.ZONE_ID);
        return Jwts.builder()
            .claims().subject(username).and()
            .claims(Collections.singletonMap(FIELD_TOKEN_ID, authTokenService.create(username, expirationDate.toLocalDateTime()).getId()))
            .issuer(jwtIssuer)
            .issuedAt(Tools.nowAsDate())
            .expiration(Date.from(expirationDate.toInstant()))
            .signWith(jwtSigningKey)
            .compact();
    }

    @Override
    public boolean validateToken(String token, @NotNull UserDetails userDetails) {
        String usernameFromAuth = userDetails.getUsername();
        try {
            jwtParser.parseSignedClaims(token);
            if (!authTokenService.exists(getAuthTokenIdFromToken(token))) {
                log.info("JWT token does not exist in db for user {}", usernameFromAuth);
                return false;
            }
            String usernameFromToken = getUsernameFromToken(token);
            if (!getUsernameFromToken(token).equals(usernameFromAuth)) {
                log.warn("username '{}' from authentication and username '{}' from JWT token does not match", usernameFromAuth, usernameFromToken);
                return false;
            }
            return getUsernameFromToken(token).equals(usernameFromAuth);
        } catch (SignatureException | MalformedJwtException e) {
            log.info("invalid JWT signature for user '{}': {}", usernameFromAuth, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("expired JWT token for user '{}': {}", usernameFromAuth, e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("unsupported JWT token for user '{}': {}", usernameFromAuth, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid for user '{}': {}", usernameFromAuth, e.getMessage());
        }
        return false;
    }
}
