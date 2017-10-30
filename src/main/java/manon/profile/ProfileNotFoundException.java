package manon.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProfileNotFoundException extends Exception {
    
    private String id;
}
