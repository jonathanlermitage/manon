package manon.api.user;

import manon.document.app.AuthTokenEntity;
import manon.util.Tools;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthAdminWSIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 3;
    }

    @Test
    public void shouldRemoveAllExpiredTokens() {
        long currentNbOfTokens = authTokenService.count();
        LocalDateTime now = Tools.now();

        login(name(1), pwd(1));
        login(name(1), pwd(1));
        login(name(2), pwd(2));
        authTokenService.create("1", Tools.yesterday());
        authTokenService.create("1", Tools.nowMinusDays(2));
        authTokenService.create("3", Tools.yesterday());

        assertThat(authTokenService.count()).isEqualTo(currentNbOfTokens + 6);

        whenAdmin().getSpec()
            .contentType(JSON)
            .delete(API_USER_ADMIN + "/auth/expired/all")
            .then()
            .statusCode(SC_OK);

        long expectedTokensKept = currentNbOfTokens + 4; // the 3 created tokens + the token used by admin to call API
        assertThat(authTokenService.count()).isEqualTo(expectedTokensKept);
        assertThat(authTokenService.findAll()).extracting(AuthTokenEntity::getExpirationDate)
            .as("only non-expired tokens are kept")
            .filteredOn(localDateTime -> localDateTime.isAfter(now))
            .hasSize((int) expectedTokensKept);
    }
}
