package manon.service.app;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

/**
 * Tools to work with JWT.
 */
public interface JwtTokenService {

    String getUsernameFromToken(String token);

    <T> T getClaimFromToken(String token, @NotNull Function<Claims, T> claimsResolver);

    String generateToken(String username);

    boolean validateToken(String token, @NotNull UserDetails userDetails);
}
