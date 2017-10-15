package manon.app.security;

import manon.user.UserAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static manon.app.config.API.API_SYS;
import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_PROFILE;
import static manon.app.config.API.API_TASK;
import static manon.app.config.API.API_USER;
import static manon.app.config.API.API_V1;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    
    private static final String ADMIN = UserAuthority.ADMIN.name();
    private static final String PLAYER = UserAuthority.PLAYER.name();
    
    private final PasswordEncoderService passwordEncoderService;
    private final UserLoaderService userLoaderService;
    
    @Autowired
    public WebSecurity(UserLoaderService userLoaderService, PasswordEncoderService passwordEncoderService) {
        this.userLoaderService = userLoaderService;
        this.passwordEncoderService = passwordEncoderService;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                
                .antMatchers(API_V1 + API_USER + "/**").hasAuthority(ADMIN) // users management
                .antMatchers(API_V1 + API_SYS + "/**").hasAuthority(ADMIN) // system configuration
                
                .antMatchers(API_V1 + API_TASK + "/**").hasAuthority(ADMIN) // system
                
                .antMatchers(POST, API_V1 + API_PROFILE).permitAll() // user registration
                .antMatchers(PUT, API_V1 + API_PROFILE + "/**").hasAuthority(PLAYER)
                .antMatchers(GET, API_V1 + API_PROFILE + "/**").hasAuthority(PLAYER)
                .antMatchers(POST, API_V1 + API_PROFILE + "/**").hasAuthority(PLAYER)
                .antMatchers(DELETE, API_V1 + API_PROFILE).hasAuthority(PLAYER)
                
                .antMatchers(API_V1 + API_LOBBY + "/**").hasAuthority(PLAYER)
                
                // actuator endpoints, see http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html
                .antMatchers("/beans").hasAuthority(ADMIN)
                .antMatchers("/configprops").hasAuthority(ADMIN)
                .antMatchers("/dump").hasAuthority(ADMIN)
                .antMatchers("/env").hasAuthority(ADMIN)
                .antMatchers("/health").permitAll()
                .antMatchers("/info").hasAuthority(ADMIN)
                .antMatchers("/metrics").hasAuthority(ADMIN)
                
                .antMatchers(API_V1 + "/**").permitAll()
                .anyRequest().denyAll()
                .and().httpBasic()
                .and().csrf().disable();
    }
    
    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers(GET, "/static/**");
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLoaderService).passwordEncoder(passwordEncoderService.getEncoder());
    }
}
