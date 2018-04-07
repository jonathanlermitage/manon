package manon.user.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class UserExistsException extends Exception {
    
    private final String username;
}
