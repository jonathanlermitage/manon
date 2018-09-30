package manon.app.security;

import lombok.RequiredArgsConstructor;
import manon.user.model.UserAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_SYS;
import static manon.app.config.API.API_USER;
import static manon.app.config.API.API_USER_ADMIN;
import static manon.app.config.API.API_WORLD;
import static manon.app.config.API.API_WORLD_ADMIN;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final String ADMIN = UserAuthority.ROLE_ADMIN.name();
    private static final String PLAYER = UserAuthority.ROLE_PLAYER.name();
    private static final String ACTUATOR = UserAuthority.ROLE_ACTUATOR.name();
    
    private final PasswordEncoderService passwordEncoderService;
    private final UserLoaderService userLoaderService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .sessionManagement().sessionCreationPolicy(STATELESS)
            .and().authorizeRequests()
            
            .antMatchers(API_SYS + "/**").hasAuthority(ADMIN)
            .antMatchers(API_USER_ADMIN + "/**").hasAuthority(ADMIN)
            .antMatchers(API_WORLD_ADMIN + "/**").hasAuthority(ADMIN)
            
            .antMatchers(POST, API_USER).permitAll() // user registration
            .antMatchers(API_USER + "/**").hasAuthority(PLAYER)
            .antMatchers(API_LOBBY + "/**").hasAuthority(PLAYER)
            .antMatchers(API_WORLD + "/**").hasAuthority(PLAYER)
            
            .antMatchers("/actuator").hasAuthority(ACTUATOR)
            .antMatchers("/actuator/**").hasAuthority(ACTUATOR)
            
            .and().httpBasic()
            .and().csrf().disable();
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
        auth.userDetailsService(userLoaderService).passwordEncoder(passwordEncoderService.getEncoder());
    }
}
