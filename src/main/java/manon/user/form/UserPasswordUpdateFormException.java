package manon.user.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserPasswordUpdateFormException extends Exception {
    
    private List<ObjectError> errors;
}
