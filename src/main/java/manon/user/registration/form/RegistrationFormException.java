package manon.user.registration.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@AllArgsConstructor
@Getter
public class RegistrationFormException extends Exception {
    
    private List<ObjectError> errors;
}
