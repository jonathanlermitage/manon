package manon.util.web;

import lombok.Data;

@Data
public class Sort {
    
    private String direction;
    private String property;
    private boolean ignoreCase;
    private String nullHandling;
    private boolean descending;
    private boolean ascending;
}
