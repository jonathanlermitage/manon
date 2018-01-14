package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static manon.util.Tools.shortenLog;

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
                "oldPassword='" + shortenLog(oldPassword) + '\'' +
                ", newPassword='" + shortenLog(newPassword) + '\'' +
                ", newPasswordCheck='" + shortenLog(newPasswordCheck) + '\'' +
                '}';
    }
}
