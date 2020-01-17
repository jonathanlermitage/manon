package manon.app.config;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Cfg cfg;

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("manon.api"))
            .paths(PathSelectors.any())
            .build().apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
            .title("Manon REST API")
            .description("Some experimentation with Spring Boot, etc.")
            .version(cfg.getVersion())
            .license("MIT")
            .licenseUrl("https://opensource.org/licenses/MIT")
            .contact(new Contact("Jonathan Lermitage", "https://github.com/jonathanlermitage/manon", "jonathan.lermitage@gmail.com"))
            .build();
    }
}
