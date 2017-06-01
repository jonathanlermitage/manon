package manon.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProfileNotFoundException extends Exception {
    
    private String[] ids;
    
    public ProfileNotFoundException(String... ids) {
        this.ids = ids;
    }
}
