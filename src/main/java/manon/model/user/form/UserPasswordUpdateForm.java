package manon.model.user.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static manon.document.user.UserEntity.Validation.PASSWORD_MAX_LENGTH;
import static manon.document.user.UserEntity.Validation.PASSWORD_MIN_LENGTH;
import static manon.document.user.UserEntity.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.util.Tools.shortenAndAnonymizeLog;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class UserPasswordUpdateForm {

    private String oldPassword;

    @NotNull(message = PASSWORD_SIZE_ERRMSG)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_SIZE_ERRMSG)
    private String newPassword;

    @Override
    public String toString() {
        return "UserPasswordUpdateForm{" +
            "oldPassword='" + shortenAndAnonymizeLog(oldPassword) + '\'' +
            ", newPassword='" + shortenAndAnonymizeLog(newPassword) + '\'' +
            '}';
    }
}
