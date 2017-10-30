package manon.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProfileUpdateFormException extends Exception {
    
    private List<ObjectError> errors;
}
