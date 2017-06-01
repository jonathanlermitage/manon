package manon.util.basetest;

import com.jayway.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rs {
    
    private RequestSpecification requestSpecification;
    private String username;
    private String password;
    
    @Override
    public String toString() {
        return "u=" + username;
    }
}
