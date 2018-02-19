package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;
import static manon.user.document.User.Validation.EMAIL_MAX_LENGTH;
import static manon.user.document.User.Validation.EMAIL_SIZE_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_MAX_LENGTH;
import static manon.user.document.User.Validation.NICKNAME_PATTERN;
import static manon.user.document.User.Validation.NICKNAME_PATTERN_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_SIZE_ERRMSG;
import static manon.util.Tools.shortenLog;

@Data
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class UserUpdateForm {
    
    @Indexed(background = true)
    @Size(max = NICKNAME_MAX_LENGTH, message = NICKNAME_SIZE_ERRMSG)
    @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_PATTERN_ERRMSG)
    private String nickname;
    
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_SIZE_ERRMSG)
    private String email;
    
    
    @Override
    public String toString() {
        return "UserUpdateForm{" +
                "nickname=" + shortenLog(nickname) +
                ", email=" + shortenLog(email) +
                '}';
    }
}
