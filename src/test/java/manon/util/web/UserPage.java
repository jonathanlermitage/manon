package manon.util.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import manon.user.document.User;

import java.util.List;

@Data
public class UserPage {
    
    @JsonSerialize(using = UserListSerializer.class)
    @JsonDeserialize(using = UserListDeserializer.class)
    private List<User> content;
    
    private Sort[] sort;
    
    private long totalElements;
    private long totalPages;
    private boolean last;
    private long size;
    private long number;
    private boolean first;
    private long numberOfElements;
}
