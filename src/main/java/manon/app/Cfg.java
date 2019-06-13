package manon.app;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString(exclude = {"defaultUserActuatorPassword", "defaultUserAdminPassword"})
public class Cfg {
    
    @Value("${info.app.version}")
    private String version;
    
    @Value("${manon.default-user.actuator.username}")
    private String defaultUserActuatorUsername;
    
    @Value("${manon.default-user.actuator.password}")
    private String defaultUserActuatorPassword;
    
    @Value("${manon.default-user.admin.username}")
    private String defaultUserAdminUsername;
    
    @Value("${manon.default-user.admin.password}")
    private String defaultUserAdminPassword;
    
    @Value("${manon.batch.user-snapshot.chunk}")
    private Integer batchUserSnapshotChunk;
    
    @Value("${manon.batch.user-snapshot.snapshot.max-age}")
    private Integer batchUserSnapshotSnapshotMaxAge;
    
    @Value("${manon.security.bcrypt.strength}")
    private Integer securityBcryptStrength;
    
    @Value("${manon.security.jwt.issuer}")
    private String securityJwtIssuer;
    
    @Value("${manon.security.jwt.signing-key-b64}")
    private String securityJwtSigningKeyB64;
    
    @Value("${manon.security.jwt.token-ttl}")
    private Long securityJwtTokenTTL;
}
