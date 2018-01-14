package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static manon.util.Tools.shortenLog;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateForm {
    
    private UserFieldEnum field;
    private Object value;
    
    @Override
    public String toString() {
        return "UserUpdateForm{" +
                "field=" + shortenLog(field) +
                ", value=" + shortenLog(value) +
                '}';
    }
}
