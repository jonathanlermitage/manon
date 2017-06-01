package manon.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProfileFieldEnum {
    
    CLANTAG("clantag"),
    EMAIL("email"),
    NICKNAME("nickname");
    
    private String fieldname;
}
