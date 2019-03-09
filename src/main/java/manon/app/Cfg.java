package manon.app;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString
public class Cfg {
    
    @Value("${manon.version}")
    private String version;
    
    @Value("${manon.admin.default-admin.username}")
    private String adminDefaultAdminUsername;
    
    @Value("${manon.admin.default-admin.password}")
    private String adminDefaultAdminPassword;
    
    @Value("${manon.batch.user-snapshot.chunk}")
    private Integer batchUserSnapshotChunk;
    
    @Value("${manon.batch.user-snapshot.snapshot.max-age}")
    private Integer batchUserSnapshotSnapshotMaxAge;
    
    @Value("${manon.security.bcrypt.strength}")
    private Integer securityBcryptStrength;
}
