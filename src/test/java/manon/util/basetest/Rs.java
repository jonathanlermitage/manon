package manon.util.basetest;

import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
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
