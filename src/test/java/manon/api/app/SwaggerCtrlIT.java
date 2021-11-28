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
    void shouldGetSwaggerJsonApiDocs(Rs rs, Integer status) {
        rs.getSpec()
            .get("/v3/api-docs")
            .then()
            .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS)
    void shouldGetSwaggerYamlApiDocs(Rs rs, Integer status) {
        rs.getSpec()
            .get("/v3/api-docs.yaml")
            .then()
            .statusCode(status);
    }
}
