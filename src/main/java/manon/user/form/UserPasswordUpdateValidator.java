package manon.user.form;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static manon.util.Tools.isBlank;

public class UserPasswordUpdateValidator implements Validator {
    
    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return UserPasswordUpdateForm.class.equals(clazz);
    }
    
    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        UserPasswordUpdateForm pwd = (UserPasswordUpdateForm) target;
        
        if (isBlank(pwd.getOldPassword())) {
            errors.reject("OLD_PASSWORD_EMPTY");
        }
        
        if (isBlank(pwd.getNewPassword())) {
            errors.reject("NEW_PASSWORD_EMPTY");
        }
        
        if (isBlank(pwd.getNewPasswordCheck())) {
            errors.reject("CHECK_PASSWORD_EMPTY");
        }
        
        if (Objects.equals(pwd.getOldPassword(), pwd.getNewPassword())) {
            errors.reject("OLD_PASSWORD_EQUALS_NEW");
        }
        
        if (!Objects.equals(pwd.getNewPassword(), pwd.getNewPasswordCheck())) {
            errors.reject("NEW_PASSWORD_NOT_CHECKED");
        }
    }
}
