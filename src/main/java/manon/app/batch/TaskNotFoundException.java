package manon.app.batch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TaskNotFoundException extends Exception {
    
    private final String name;
}
