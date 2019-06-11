package manon.model.user.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;
import static manon.document.user.User.Validation.PASSWORD_MAX_LENGTH;
import static manon.document.user.User.Validation.PASSWORD_MIN_LENGTH;
import static manon.document.user.User.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.document.user.User.Validation.USERNAME_MAX_LENGTH;
import static manon.document.user.User.Validation.USERNAME_MIN_LENGTH;
import static manon.document.user.User.Validation.USERNAME_PATTERN;
import static manon.document.user.User.Validation.USERNAME_PATTERN_ERRMSG;
import static manon.document.user.User.Validation.USERNAME_SIZE_ERRMSG;
import static manon.util.Tools.shortenAndAnonymizeLog;
import static manon.util.Tools.shortenLog;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class UserLogin {
    
    @NotNull(message = USERNAME_SIZE_ERRMSG)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = USERNAME_SIZE_ERRMSG)
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_PATTERN_ERRMSG)
    private String username;
    
    @NotNull(message = PASSWORD_SIZE_ERRMSG)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_SIZE_ERRMSG)
    private String password;
    
    @Override
    public String toString() {
        return "UserLogin{" +
            "username='" + shortenLog(username) + '\'' +
            ", password='" + shortenAndAnonymizeLog(password) + '\'' +
            '}';
    }
}
