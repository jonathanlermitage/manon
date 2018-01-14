package manon.user.registration.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static manon.util.Tools.shortenLog;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    
    private String username;
    private String password;
    
    @Override
    public String toString() {
        return "RegistrationForm{" +
                "username='" + shortenLog(username) + '\'' +
                ", password='" + shortenLog(password) + '\'' +
                '}';
    }
}
