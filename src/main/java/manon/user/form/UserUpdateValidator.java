package manon.user.form;

import manon.user.document.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserUpdateValidator implements Validator {
    
    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return UserUpdateForm.class.equals(clazz);
    }
    
    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        UserUpdateForm form = (UserUpdateForm) target;
        
        switch (form.getField()) {
            
            case EMAIL:
                if (!(form.getValue() instanceof String)) {
                    errors.reject("EMAIL_BAD_OBJECTCLASS");
                    break;
                }
                String email = (String) form.getValue();
                if (email != null && !email.trim().isEmpty()) {
                    if (email.length() > User.Validation.EMAIL_MAX_LENGTH) {
                        errors.reject("EMAIL_TOO_LONG");
                    } else if (!User.Validation.EMAIL_PATTERN.matcher(email).find()) {
                        errors.reject("EMAIL_BAD_FORMAT");
                    }
                }
                break;
            
            case NICKNAME:
                if (!(form.getValue() instanceof String)) {
                    errors.reject("NICKNAME_BAD_OBJECTCLASS");
                    break;
                }
                String nickname = (String) form.getValue();
                if (nickname != null && !nickname.trim().isEmpty()) {
                    if (nickname.length() > User.Validation.NICKNAME_MAX_LENGTH) {
                        errors.reject("NICKNAME_TOO_LONG");
                    } else if (!User.Validation.NICKNAME_PATTERN.matcher(nickname).find()) {
                        errors.reject("NICKNAME_BAD_FORMAT");
                    }
                }
                break;
        }
    }
}
