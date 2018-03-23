package manon.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserExistsException extends Exception {
    
    private final String username;
}
