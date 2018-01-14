package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserFieldEnum {
    
    EMAIL("email"),
    NICKNAME("nickname");
    
    private String fieldname;
}
