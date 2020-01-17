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

import java.security.Key;
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
    private final Key jwtSigningKey;
    private final JwtParser jwtParser;
    private final AuthTokenService authTokenService;

    public JwtTokenServiceImpl(@NotNull Cfg cfg, AuthTokenService authTokenService) {
        this.cfg = cfg;
        this.jwtIssuer = cfg.getSecurityJwtIssuer();
        this.jwtSigningKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(cfg.getSecurityJwtSigningKeyB64()));
        this.jwtParser = Jwts.parser().setSigningKey(jwtSigningKey);
        this.authTokenService = authTokenService;
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private long getAuthTokenIdFromToken(String token) {
        return getClaimFromToken(token, claims -> Long.parseLong(claims.get(FIELD_TOKEN_ID).toString()));
    }

    @Override
    public <T> T getClaimFromToken(String token, @NotNull Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(cfg.getSecurityJwtSigningKeyB64())
            .parseClaimsJws(token)
            .getBody();
    }

    @Override
    public String generateToken(String username) {
        ZonedDateTime expirationDate = Tools.now().plus(cfg.getSecurityJwtTokenTtl()).atZone(Tools.ZONE_ID);
        return Jwts.builder()
            .setClaims(Jwts.claims().setSubject(username))
            .addClaims(Collections.singletonMap(FIELD_TOKEN_ID, authTokenService.create(username, expirationDate.toLocalDateTime()).getId()))
            .setIssuer(jwtIssuer)
            .setIssuedAt(Tools.nowAsDate())
            .setExpiration(Date.from(expirationDate.toInstant()))
            .signWith(jwtSigningKey)
            .compact();
    }

    @Override
    public boolean validateToken(String token, @NotNull UserDetails userDetails) {
        String username = userDetails.getUsername();
        try {
            jwtParser.parseClaimsJws(token);
            if (!authTokenService.exists(getAuthTokenIdFromToken(token))) {
                return false;
            }
            return getUsernameFromToken(token).equals(username);
        } catch (SignatureException | MalformedJwtException e) {
            log.info("invalid JWT signature for user {}: {}" + username, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("expired JWT token for user {}: {}" + username, e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("unsupported JWT token for user {}: {}" + username, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid for user {}: {}" + username, e.getMessage());
        }
        return false;
    }
}
