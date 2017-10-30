package manon.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskNotFoundException extends Exception {
    
    private String name;
}
