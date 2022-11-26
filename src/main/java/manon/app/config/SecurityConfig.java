package manon.app.config;

import lombok.RequiredArgsConstructor;
import manon.model.user.UserRole;
import manon.service.user.PasswordEncoderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static manon.app.Globals.API.API_BASE;
import static manon.app.Globals.API.API_SYS;
import static manon.app.Globals.API.API_USER;
import static manon.app.Globals.API.API_USER_ADMIN;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// Migrated to Spring Security 5.7: WebSecurityConfigurerAdapter has been deprecated.
// See https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter for help with migration.
@Configuration
@EnableWebSecurity
@Order(HIGHEST_PRECEDENCE)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ACTUATOR = UserRole.ACTUATOR.name();
    private static final String ADMIN = UserRole.ADMIN.name();
    private static final String PLAYER = UserRole.PLAYER.name();

    @Bean
    public AuthenticationManager globalAuthenticationManagerBean(ObjectPostProcessor<Object> objectPostProcessor,
                                                                 UserDetailsService userDetailsService,
                                                                 PasswordEncoderService passwordEncoderService) throws Exception {
        return new AuthenticationManagerBuilder(objectPostProcessor)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoderService.getEncoder())
            .and().build();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                                          JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(withDefaults())

            .sessionManagement(httpSecurity -> httpSecurity.sessionCreationPolicy(STATELESS))

            .exceptionHandling(httpSecurity -> httpSecurity.authenticationEntryPoint(jwtAuthenticationEntryPoint))

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(a -> a
                .requestMatchers(API_SYS + "/**").hasRole(ADMIN)

                .requestMatchers(API_USER_ADMIN + "/**").hasRole(ADMIN)

                .requestMatchers(POST, API_USER).permitAll() // user registration
                .requestMatchers(POST, API_USER + "/auth/authorize").permitAll() // user authentication
                .requestMatchers(POST, API_USER + "/auth/**").authenticated() // token renewal and logout
                .requestMatchers(API_USER + "/**").hasRole(PLAYER)

                .requestMatchers(API_BASE + "/**").authenticated()

                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .requestMatchers("/actuator").hasRole(ACTUATOR)
                .requestMatchers("/actuator/**").hasRole(ACTUATOR)
                .requestMatchers("/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/swagger-config",
                    "/v3/api-docs.yaml").permitAll()

                // allow access to '/error', otherwise it's protected by spring-security and error bodies are blank
                .requestMatchers("/error").permitAll()

                .anyRequest().denyAll()
            ).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(
            "Accept", "Origin", "Content-Type", "Depth", "User-Agent", "If-Modified-Since,",
            "Cache-Control", "Authorization", "X-Req", "X-File-Size", "X-Requested-With", "X-File-Name"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
