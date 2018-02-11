package manon.app.security;

import lombok.RequiredArgsConstructor;
import manon.user.UserAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_SYS;
import static manon.app.config.API.API_TASK;
import static manon.app.config.API.API_USER;
import static manon.app.config.API.API_USER_ADMIN;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final String ADMIN = UserAuthority.ADMIN.name();
    private static final String PLAYER = UserAuthority.PLAYER.name();
    
    private final PasswordEncoderService passwordEncoderService;
    private final UserLoaderService userLoaderService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeRequests()
                
                .antMatchers(API_SYS + "/**").hasAuthority(ADMIN)
                .antMatchers(API_TASK + "/**").hasAuthority(ADMIN)
                .antMatchers(API_USER_ADMIN + "/**").hasAuthority(ADMIN)
                
                .antMatchers(POST, API_USER).permitAll() // user registration
                .antMatchers(API_USER + "/**").hasAuthority(PLAYER)
                .antMatchers(API_LOBBY + "/**").hasAuthority(PLAYER)
                
                .antMatchers("/actuator").hasAuthority(ADMIN)
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/info").permitAll()
                .antMatchers("/actuator/**").hasAuthority(ADMIN)
                
                .anyRequest().denyAll()
                .and().httpBasic()
                .and().csrf().disable();
    }
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(OPTIONS, "/**")
                .antMatchers(GET, "/static/**");
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLoaderService).passwordEncoder(passwordEncoderService.getEncoder());
    }
}
