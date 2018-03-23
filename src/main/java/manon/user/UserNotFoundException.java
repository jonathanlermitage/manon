package manon.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserNotFoundException extends Exception {
    
    private final String id;
}
