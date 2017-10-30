package manon.profile;

import manon.profile.document.Profile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ProfileUpdateValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return ProfileUpdateForm.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        ProfileUpdateForm form = (ProfileUpdateForm) target;
        
        switch (form.getField()) {
            
            case CLANTAG:
                if (!(form.getValue() instanceof String)) {
                    errors.reject("CLANTAG_BAD_OBJECTCLASS");
                    break;
                }
                String clantag = (String) form.getValue();
                if (clantag != null && !clantag.trim().isEmpty()) {
                    if (clantag.length() > Profile.Validation.CLANTAG_MAX_LENGTH) {
                        errors.reject("CLANTAG_TOO_LONG");
                    } else if (!Profile.Validation.CLANTAG_PATTERN.matcher(clantag).find()) {
                        errors.reject("CLANTAG_BAD_FORMAT");
                    }
                }
                break;
            
            case EMAIL:
                if (!(form.getValue() instanceof String)) {
                    errors.reject("EMAIL_BAD_OBJECTCLASS");
                    break;
                }
                String email = (String) form.getValue();
                if (email != null && !email.trim().isEmpty()) {
                    if (email.length() > Profile.Validation.EMAIL_MAX_LENGTH) {
                        errors.reject("EMAIL_TOO_LONG");
                    } else if (!Profile.Validation.EMAIL_PATTERN.matcher(email).find()) {
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
                    if (nickname.length() > Profile.Validation.NICKNAME_MAX_LENGTH) {
                        errors.reject("NICKNAME_TOO_LONG");
                    } else if (!Profile.Validation.NICKNAME_PATTERN.matcher(nickname).find()) {
                        errors.reject("NICKNAME_BAD_FORMAT");
                    }
                }
                break;
            
            default:
                errors.reject("BAD_FIELDNAME"); // TODO test BAD_FIELDNAME use case
        }
    }
}
