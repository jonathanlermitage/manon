package manon.util.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import manon.user.document.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPage {
    
    @JsonSerialize(using = UserListSerializer.class)
    @JsonDeserialize(using = UserListDeserializer.class)
    private List<User> content;
    
    private long totalElements;
    private long totalPages;
    private boolean last;
    private long size;
    private long number;
    private boolean first;
    private long numberOfElements;
}
