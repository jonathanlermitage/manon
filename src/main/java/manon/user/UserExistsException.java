package manon.user;

import lombok.Getter;

@Getter
public class UserExistsException extends Exception {
    
    private String username;
    
    public UserExistsException(String username) {
        this.username = username;
    }
}
