package manon.document.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.model.user.RegistrationState;
import manon.util.Tools;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static manon.document.user.UserEntity.Validation.EMAIL_MAX_LENGTH;
import static manon.document.user.UserEntity.Validation.EMAIL_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.NICKNAME_MAX_LENGTH;
import static manon.document.user.UserEntity.Validation.NICKNAME_PATTERN;
import static manon.document.user.UserEntity.Validation.NICKNAME_PATTERN_ERRMSG;
import static manon.document.user.UserEntity.Validation.NICKNAME_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.PASSWORD_MAX_LENGTH;
import static manon.document.user.UserEntity.Validation.PASSWORD_MIN_LENGTH;
import static manon.document.user.UserEntity.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.USERNAME_MAX_LENGTH;
import static manon.document.user.UserEntity.Validation.USERNAME_MIN_LENGTH;
import static manon.document.user.UserEntity.Validation.USERNAME_PATTERN;
import static manon.document.user.UserEntity.Validation.USERNAME_PATTERN_ERRMSG;
import static manon.document.user.UserEntity.Validation.USERNAME_SIZE_ERRMSG;

@Entity(name = "User")
@Table(name = "user_") // 'user' is a reserved keyword in some db, like PostgreSQL
@QueryEntity
@Getter
@ToString(exclude = "userSnapshots")
@EqualsAndHashCode(exclude = {"id", "userSnapshots", "version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 443313310250932570L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Unique, uppercase and not modifiable login name. */
    @NotNull(message = USERNAME_SIZE_ERRMSG)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = USERNAME_SIZE_ERRMSG)
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_PATTERN_ERRMSG)
    @Column(nullable = false, length = USERNAME_MAX_LENGTH, unique = true, updatable = false)
    private String username;

    /** Comma separated list of authorities. */
    @Column(nullable = false)
    private String authorities;

    @JsonIgnore
    @NotNull(message = PASSWORD_SIZE_ERRMSG)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_SIZE_ERRMSG)
    @Column(nullable = false, length = PASSWORD_MAX_LENGTH)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationState registrationState;

    /** A non-unique name that can change, NOT mandatory. */
    @Size(max = NICKNAME_MAX_LENGTH, message = NICKNAME_SIZE_ERRMSG)
    @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_PATTERN_ERRMSG)
    @Column(length = NICKNAME_MAX_LENGTH)
    private String nickname;

    /** Email. */
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_SIZE_ERRMSG)
    @Column(length = EMAIL_MAX_LENGTH)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserSnapshotEntity> userSnapshots;

    @Version
    @Column(nullable = false)
    private long version;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = Tools.now();
        if (creationDate == null) {
            creationDate = now;
        }
        updateDate = now;
    }

    /** {@link UserEntity} fields validation rules. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Validation {
        public static final String USERNAME_PATTERN = "^[A-Z0-9_\\-\\u0020]*$";
        public static final String USERNAME_PATTERN_ERRMSG = "USERNAME_PATTERN";

        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 24;
        public static final String USERNAME_SIZE_ERRMSG = "USERNAME_SIZE";

        public static final String NICKNAME_PATTERN = "^[a-zA-Z0-9_\\-\\u0020]*$";
        public static final String NICKNAME_PATTERN_ERRMSG = "NICKNAME_PATTERN";

        public static final int NICKNAME_MAX_LENGTH = 24;
        public static final String NICKNAME_SIZE_ERRMSG = "NICKNAME_SIZE";

        public static final int EMAIL_MAX_LENGTH = 256;
        public static final String EMAIL_SIZE_ERRMSG = "EMAIL_SIZE";

        public static final int PASSWORD_MIN_LENGTH = 4;
        public static final int PASSWORD_MAX_LENGTH = 256;
        public static final String PASSWORD_SIZE_ERRMSG = "PASSWORD_SIZE";
    }
}
