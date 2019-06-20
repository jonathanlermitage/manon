package manon.api.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.service.app.AuthTokenService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_USER_ADMIN;

/** User API. */
@Api(description = "Authentication administration tasks. Used by: admin.")
@RestController
@RequestMapping(value = API_USER_ADMIN)
@RequiredArgsConstructor
@Slf4j
public class AuthAdminWS {
    
    private final AuthTokenService authTokenService;
    
    @ApiOperation(value = "Maintenance: remove all expired authentication token references from database.")
    @DeleteMapping(value = "/auth/expired/all")
    public void removeAllExpiredTokens() {
        authTokenService.removeAllExpired();
    }
}
