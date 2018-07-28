package manon.app.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import manon.user.document.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class UserSimpleDetails implements UserDetails, Serializable {
    
    private static final long serialVersionUID = -7532767239139493489L;
    
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
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
