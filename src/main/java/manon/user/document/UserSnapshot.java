package manon.user.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.user.model.RegistrationState;
import manon.util.Tools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import static manon.util.Tools.DATE_FORMAT;

@Entity
@Getter
@ToString
@EqualsAndHashCode(exclude = "creationDate")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserSnapshot implements Serializable {
    
    private static final long serialVersionUID = -4321502988403908385L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(updatable = false)
    private long userId;
    
    @Column(updatable = false)
    private String userUsername;
    
    @Column(updatable = false)
    private String userAuthorities;
    
    @Column(updatable = false)
    private String userPassword;
    
    @Column(updatable = false)
    private RegistrationState userRegistrationState;
    
    @Column(updatable = false)
    private String userNickname;
    
    @Column(updatable = false)
    private String userEmail;
    
    @Column(updatable = false)
    private long userVersion;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;
    
    @PrePersist
    public void prePersist() {
        Date now = Tools.now();
        if (creationDate == null) {
            creationDate = now;
        }
    }
    
    /** Populate a {@link UserSnapshot} from a {@link User}. */
    public static UserSnapshot from(User user) {
        return UserSnapshot.builder()
            .userId(user.getId())
            .userUsername(user.getUsername())
            .userAuthorities(user.getAuthorities())
            .userPassword(user.getPassword())
            .userRegistrationState(user.getRegistrationState())
            .userNickname(user.getNickname())
            .userEmail(user.getEmail())
            .userVersion(user.getVersion())
            .build();
    }
}
