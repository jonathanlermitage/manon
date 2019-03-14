package manon.util.web;

import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor
public class Rs {
    
    private RequestSpecification requestSpecification;
    private String username;
    private String password;
    
    @NotNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return username + ":" + password;
    }
}
