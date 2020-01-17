package manon.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** User roles. */
@AllArgsConstructor
public enum UserRole {

    ACTUATOR("ROLE_ACTUATOR"),
    ADMIN("ROLE_ADMIN"),
    PLAYER("ROLE_PLAYER");

    @Getter
    private String authority;
}
