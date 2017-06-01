package manon.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserFieldEnum {
    
    PASSWORD("password"),
    REGISTRATION_STATE("registrationState");
    
    private String fieldname;
}
