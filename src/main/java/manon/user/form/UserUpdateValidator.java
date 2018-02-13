package manon.user.form;

import manon.user.document.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static manon.util.Tools.isBlank;

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
                if (form.getValue() == null) {
                    errors.reject("EMAIL_NULL");
                } else if (!(form.getValue() instanceof String)) {
                    errors.reject("EMAIL_BAD_CLASS");
                    break;
                } else {
                    String email = (String) form.getValue();
                    if (!isBlank(email)) {
                        if (email.length() > User.Validation.EMAIL_MAX_LENGTH) {
                            errors.reject("EMAIL_TOO_LONG");
                        } else if (!User.Validation.EMAIL_PATTERN.matcher(email).find()) {
                            errors.reject("EMAIL_BAD_FORMAT");
                        }
                    }
                }
                break;
            
            case NICKNAME:
                if (form.getValue() == null) {
                    errors.reject("NICKNAME_NULL");
                } else if (!(form.getValue() instanceof String)) {
                    errors.reject("NICKNAME_BAD_CLASS");
                    break;
                } else {
                    String nickname = (String) form.getValue();
                    if (!isBlank(nickname)) {
                        if (nickname.length() > User.Validation.NICKNAME_MAX_LENGTH) {
                            errors.reject("NICKNAME_TOO_LONG");
                        } else if (!User.Validation.NICKNAME_PATTERN.matcher(nickname).find()) {
                            errors.reject("NICKNAME_BAD_FORMAT");
                        }
                    }
                }
        }
    }
}
