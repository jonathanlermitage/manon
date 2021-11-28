package manon.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserSimpleDetails;
import manon.model.user.form.UserLogin;
import manon.service.app.AuthTokenService;
import manon.service.app.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_USER;

/** User API. */
@Tag(name = "Manage authentication. Used by: registered users, except authorization that is public.")
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class AuthWS {

    private final AuthenticationManager authenticationManager;
    private final AuthTokenService authTokenService;
    private final JwtTokenService jwtTokenService;

    @Operation(summary = "Authenticate and get a JWT token for me.")
    @PostMapping(value = "/auth/authorize")
    public String createAuthToken(@RequestBody @Validated UserLogin userLogin) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            throw new UserNotFoundException();
        }
        return jwtTokenService.generateToken(userLogin.getUsername());
    }

    @Operation(summary = "Generate a new JWT token for me.")
    @PostMapping(value = "/auth/renew")
    public String renewAuthToken(@AuthenticationPrincipal UserSimpleDetails user) {
        return jwtTokenService.generateToken(user.getUsername());
    }

    @Operation(summary = "Invaldate all my JWT tokens.")
    @PostMapping(value = "/auth/logout/all")
    public void logoutAll(@AuthenticationPrincipal UserSimpleDetails user) {
        authTokenService.removeUserTokens(user.getUsername());
    }
}
