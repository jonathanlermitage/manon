package manon.app.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import manon.service.app.JwtTokenService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilterConfig extends OncePerRequestFilter {
    
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = jwtTokenService.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.info("an error occured during getting username from token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.info("the token is expired and not valid anymore: " + e.getMessage());
            } catch (SecurityException e) {
                logger.info("authentication failed, username or password not valid: " + e.getMessage());
            }
        } else {
            logger.debug("couldn't find bearer string, will ignore the header");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenService.validateToken(authToken, userDetails)
                && userDetails.isEnabled()
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.debug("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(req, res);
    }
}
