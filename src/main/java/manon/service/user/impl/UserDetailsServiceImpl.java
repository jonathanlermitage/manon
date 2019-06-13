package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.User;
import manon.model.user.UserSimpleDetails;
import manon.service.user.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static manon.model.user.RegistrationState.ACTIVE;

@Service
@Primary
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserService userService;
    
    @Override
    public UserSimpleDetails loadUserByUsername(final String username) {
        User user = userService.readByUsername(username);
        
        return new UserSimpleDetails(
            user.getUsername(),
            user.getPassword(),
            true, true, true,
            ACTIVE == user.getRegistrationState(),
            Stream.of(user.getAuthorities().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toSet()),
            user);
    }
}
