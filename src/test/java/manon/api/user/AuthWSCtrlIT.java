package manon.api.user;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthWSCtrlIT extends AbstractMockIT {

    public Object[][] dataProviderAllowActiveUsersWithCredentials() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenActuator(), SC_OK},
            {whenP1(), SC_OK},
            {whenBannedReactivated(), SC_OK},
            {whenDeletedReactivated(), SC_OK},
            {whenSuspendedReactivated(), SC_OK},
            {whenBanned(), SC_UNAUTHORIZED},
            {whenDeleted(), SC_UNAUTHORIZED},
            {whenSuspended(), SC_UNAUTHORIZED}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderAllowActiveUsersWithCredentials")
    public void shouldVerifyCreateAuthToken(Rs rs, Integer status) {
        login(rs.getUsername(), rs.getPassword())
            .then()
            .statusCode(status);
    }

    @Test
    public void shouldVerifyCreateAuthTokenWhenAnonymous() {
        whenAnonymous().getSpec()
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("dataProviderAllowActiveUsersWithCredentials")
    public void shouldVerifyRenewAuthToken(Rs rs, Integer status) {
        String jwt = jwtTokenService.generateToken(rs.getUsername());
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .post(API_USER + "/auth/renew")
            .then()
            .statusCode(status);
    }

    @Test
    public void shouldVerifyRenewAuthTokenWhenAnonymous() {
        whenAnonymous().getSpec()
            .post(API_USER + "/auth/renew")
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("dataProviderAllowActiveUsersWithCredentials")
    public void shouldVerifyLogoutAll(Rs rs, Integer status) {
        String jwt = jwtTokenService.generateToken(rs.getUsername());
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .post(API_USER + "/auth/logout/all")
            .then()
            .statusCode(status);
    }

    @Test
    public void shouldVerifyLogoutAllWhenAnonymous() {
        whenAnonymous().getSpec()
            .post(API_USER + "/auth/logout/all")
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }
}
