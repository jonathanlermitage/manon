package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static manon.util.Tools.shortenAndAnonymizeLog;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordUpdateForm {
    
    private String oldPassword;
    private String newPassword;
    private String newPasswordCheck;
    
    @Override
    public String toString() {
        return "UserPasswordUpdateForm{" +
                "oldPassword='" + shortenAndAnonymizeLog(oldPassword) + '\'' +
                ", newPassword='" + shortenAndAnonymizeLog(newPassword) + '\'' +
                ", newPasswordCheck='" + shortenAndAnonymizeLog(newPasswordCheck) + '\'' +
                '}';
    }
}
