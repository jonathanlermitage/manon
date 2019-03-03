package manon.util.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page<T> {
    
    private List<T> content;
    
    private long totalElements;
    private long totalPages;
    private boolean last;
    private long size;
    private long number;
    private boolean first;
    private long numberOfElements;
}
