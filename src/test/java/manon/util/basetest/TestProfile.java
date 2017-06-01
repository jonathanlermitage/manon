package manon.util.basetest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestProfile {
    
    public String userId;
    public String profileId;
    public String name;
    public String pwd;
}
