package manon.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/** Enable Cross-Origin Resource Sharing. */
@Configuration
public class RestConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("Accept, Origin, Content-Type, Depth, User-Agent, If-Modified-Since, " +
                "Cache-Control, Authorization, X-Req, X-File-Size, X-Requested-With, X-File-Name");
        config.addAllowedMethod(HttpMethod.OPTIONS);
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
