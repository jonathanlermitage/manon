package manon.app.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.user.document.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class UserSimpleDetails implements UserDetails, Serializable {
    
    private String username;
    private String password;
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;
    private Collection<? extends GrantedAuthority> authorities;
    private User user;
    
    // Accelerators.
    
    public String getProfileId() {
        return getUser().getProfileId();
    }
    
    public String getUserId() {
        return getUser().getId();
    }
    
    public String getIdentity() {
        return getUsername() + " (profile " + getProfileId() + ")";
    }
}
