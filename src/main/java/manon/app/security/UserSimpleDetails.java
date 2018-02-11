package manon.app.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import manon.user.document.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class UserSimpleDetails implements UserDetails, Serializable {
    
    private String username;
    private String password;
    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private boolean accountNonExpired = true;
    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private boolean accountNonLocked = true;
    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private boolean enabled = true;
    private Collection<? extends GrantedAuthority> authorities;
    private User user;
    
    // Accelerators.
    
    @JsonIgnore
    public String getUserId() {
        return getUser().getId();
    }
    
    @JsonIgnore
    public String getIdentity() {
        return getUsername() + " (id " + getUserId() + ")";
    }
}
