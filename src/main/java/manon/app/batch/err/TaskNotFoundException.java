package manon.app.batch.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class TaskNotFoundException extends Exception {
    
    private final String name;
}
