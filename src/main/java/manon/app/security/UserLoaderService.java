package manon.app.security;

import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import manon.user.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class UserLoaderService implements UserDetailsService {
    
    private final UserService userService;
    
    @Override
    public UserSimpleDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userService.readByUsername(username);
        return UserSimpleDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name())).collect(Collectors.toSet()))
                .enabled(RegistrationStateEnum.ACTIVE == user.getRegistrationState())
                .user(user)
                .build();
    }
}
