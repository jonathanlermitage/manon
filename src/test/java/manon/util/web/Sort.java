package manon.util.web;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class Sort {
    
    private String direction;
    private String property;
    private boolean ignoreCase;
    private String nullHandling;
    private boolean descending;
    private boolean ascending;
}
