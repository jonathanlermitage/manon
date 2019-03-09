package manon.document.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.model.user.RegistrationState;
import manon.util.Tools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime creationDate;
    
    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = Tools.now();
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
