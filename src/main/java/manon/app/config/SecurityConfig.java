package manon.app.config;

import lombok.RequiredArgsConstructor;
import manon.model.user.UserRole;
import manon.service.user.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
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

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, order = HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ACTUATOR = UserRole.ACTUATOR.name();
    private static final String ADMIN = UserRole.ADMIN.name();
    private static final String PLAYER = UserRole.PLAYER.name();

    private final PasswordEncoderService passwordEncoderService;
    private final JwtAuthenticationEntryPointConfig jwtAuthenticationEntryPointConfig;
    private final JwtAuthenticationFilterConfig jwtAuthenticationFilterConfig;
    private final UserDetailsService userDetailsService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(withDefaults())

            .sessionManagement(httpSecurity -> httpSecurity.sessionCreationPolicy(STATELESS))

            .exceptionHandling(httpSecurity -> httpSecurity.authenticationEntryPoint(jwtAuthenticationEntryPointConfig))

            .addFilterBefore(jwtAuthenticationFilterConfig, UsernamePasswordAuthenticationFilter.class)

            .authorizeRequests(a -> a
                .antMatchers(API_SYS + "/**").hasRole(ADMIN)

                .antMatchers(API_USER_ADMIN + "/**").hasRole(ADMIN)

                .antMatchers(POST, API_USER).permitAll() // user registration
                .antMatchers(POST, API_USER + "/auth/authorize").permitAll() // user authentication
                .antMatchers(POST, API_USER + "/auth/**").authenticated() // token renewal and logout
                .antMatchers(API_USER + "/**").hasRole(PLAYER)

                .antMatchers(API_BASE + "/**").authenticated()

                .antMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .antMatchers("/actuator").hasRole(ACTUATOR)
                .antMatchers("/actuator/**").hasRole(ACTUATOR)
                .antMatchers("/swagger-resources",
                    "/swagger-resources/configuration/ui",
                    "/swagger-resources/configuration/security",
                    "/swagger-ui.html",
                    "/swagger-ui/",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/v2/api-docs").permitAll()

                .anyRequest().denyAll()
            );
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

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderService.getEncoder());
    }
}
