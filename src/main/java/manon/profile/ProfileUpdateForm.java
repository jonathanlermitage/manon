package manon.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class ProfileUpdateForm {
    
    private ProfileFieldEnum field;
    private Object value;
}
