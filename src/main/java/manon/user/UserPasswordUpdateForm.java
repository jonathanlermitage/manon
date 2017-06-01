package manon.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class UserPasswordUpdateForm {
    
    private String oldPassword;
    private String newPassword;
    private String newPasswordCheck;
}
