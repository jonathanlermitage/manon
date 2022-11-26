package manon.document.app;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.util.Tools;

import java.io.Serializable;
import java.time.LocalDateTime;

import static manon.document.user.UserEntity.Validation.USERNAME_MAX_LENGTH;

@Entity(name = "AuthToken")
@Table(name = "auth_token")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"id", "creationDate"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenEntity implements Serializable {

    private static final long serialVersionUID = -2786555151682984356L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = USERNAME_MAX_LENGTH)
    private String username;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime expirationDate;

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
}
