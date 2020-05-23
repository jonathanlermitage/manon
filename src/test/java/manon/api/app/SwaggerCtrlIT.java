package manon.api.app;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SwaggerCtrlIT extends AbstractMockIT {

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerIndex(Rs rs, Integer status) {
        rs.getSpec()
            .get("/swagger-ui.html")
            .then()
            .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerApiDocs(Rs rs, Integer status) {
        rs.getSpec()
            .get("/v2/api-docs")
            .then()
            .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerResourcesConfigurationUi(Rs rs, Integer status) {
        rs.getSpec()
            .get("/swagger-resources/configuration/ui")
            .then()
            .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerResourcesConfigurationSecurity(Rs rs, Integer status) {
        rs.getSpec()
            .get("/swagger-resources/configuration/security")
            .then()
            .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerWebjars(Rs rs, Integer status) {
        rs.getSpec()
            .get("/webjars/springfox-swagger-ui/springfox.css")
            .then()
            .statusCode(status);
    }
}
