package manon.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserExistsException extends Exception {
    
    private String username;
}
