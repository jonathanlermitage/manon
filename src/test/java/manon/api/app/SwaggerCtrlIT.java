package manon.api.app;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SwaggerCtrlIT extends AbstractMockIT {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_DEV)
    public void shouldGetSwaggerIndex(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get("/swagger-ui.html")
            .then()
            .statusCode(status);
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_DEV)
    public void shouldGetSwaggerApiDocs(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get("/v2/api-docs")
            .then()
            .statusCode(status);
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_DEV)
    public void shouldGetSwaggerResourcesConfigurationUi(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get("/swagger-resources/configuration/ui")
            .then()
            .statusCode(status);
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_DEV)
    public void shouldGetSwaggerResourcesConfigurationSecurity(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get("/swagger-resources/configuration/security")
            .then()
            .statusCode(status);
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_DEV)
    public void shouldGetSwaggerWebjars(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get("/webjars/springfox-swagger-ui/springfox.css")
            .then()
            .statusCode(status);
    }
}
