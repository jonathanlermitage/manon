package manon.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Cfg cfg;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Manon REST API")
                .description("Some experimentation with Spring Boot, etc.")
                .version(cfg.getVersion())
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
