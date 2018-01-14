package manon.util.web;

import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserPasswordUpdateFormException;
import manon.user.form.UserPasswordUpdateValidator;
import manon.user.form.UserUpdateForm;
import manon.user.form.UserUpdateFormException;
import manon.user.form.UserUpdateValidator;
import manon.user.registration.form.RegistrationForm;
import manon.user.registration.form.RegistrationFormException;
import manon.user.registration.form.RegistrationValidator;
import org.springframework.validation.BindingResult;

import static org.springframework.validation.ValidationUtils.invokeValidator;

public abstract class ValidationUtils {
    
    private static final UserUpdateValidator userUpdateValidator = new UserUpdateValidator();
    private static final RegistrationValidator registrationValidator = new RegistrationValidator();
    private static final UserPasswordUpdateValidator userPasswordUpdateValidator = new UserPasswordUpdateValidator();
    
    public static void validate(UserUpdateForm form, BindingResult bindingResult) throws UserUpdateFormException {
        invokeValidator(userUpdateValidator, form, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserUpdateFormException(bindingResult.getAllErrors());
        }
    }
    
    public static void validate(RegistrationForm form, BindingResult bindingResult) throws RegistrationFormException {
        invokeValidator(registrationValidator, form, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new RegistrationFormException(bindingResult.getAllErrors());
        }
    }
    
    public static void validate(UserPasswordUpdateForm form, BindingResult bindingResult) throws UserPasswordUpdateFormException {
        invokeValidator(userPasswordUpdateValidator, form, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserPasswordUpdateFormException(bindingResult.getAllErrors());
        }
    }
}
