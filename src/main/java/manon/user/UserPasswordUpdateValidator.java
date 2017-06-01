package manon.user;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static manon.util.Tools.isBlank;

public class UserPasswordUpdateValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return UserPasswordUpdateForm.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        UserPasswordUpdateForm pwd = (UserPasswordUpdateForm) target;
        
        if (isBlank(pwd.getOldPassword())) {
            errors.reject("OLD_PASSWORD_EMPTY");
        } else if (isBlank(pwd.getNewPassword())) {
            errors.reject("NEW_PASSWORD_EMPTY");
        } else if (isBlank(pwd.getNewPasswordCheck())) {
            errors.reject("CHECK_PASSWORD_EMPTY");
        } else if (pwd.getOldPassword().equals(pwd.getNewPassword())) {
            errors.reject("OLD_PASSWORD_EQUALS_NEW");
        } else if (!pwd.getNewPassword().equals(pwd.getNewPasswordCheck())) {
            errors.reject("NEW_PASSWORD_NOT_CHECKED");
        }
    }
}
