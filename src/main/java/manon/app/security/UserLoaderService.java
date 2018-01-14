package manon.app.security;

import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import manon.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

@Configuration
public class UserLoaderService implements UserDetailsService {
    
    private final UserService userService;
    
    @Autowired
    public UserLoaderService(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public UserSimpleDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userService.readByUsername(username);
        return UserSimpleDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name())).collect(Collectors.toList()))
                .enabled(RegistrationStateEnum.ACTIVE == user.getRegistrationState())
                .user(user)
                .build();
    }
}
