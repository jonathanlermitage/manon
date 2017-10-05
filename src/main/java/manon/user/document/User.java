package manon.user.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.user.UserAuthority;
import manon.user.registration.RegistrationStateEnum;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

/**
 * Registered user.
 * Used for registration and authentication only, see {@link manon.profile.document.Profile} for more relevant data.
 */
@Document(collection = "User")
@TypeAlias("User")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class User implements Serializable {
    
    @Id
    private String id;
    
    /** Unique, non case-sensitive and not modifiable login name. */
    @NotEmpty
    @Indexed(background = true, unique = true)
    private String username;
    
    @NotNull
    private List<UserAuthority> roles;
    
    @NotEmpty
    @JsonIgnore
    private String password;
    
    @NotNull
    private RegistrationStateEnum registrationState;
    
    private String profileId;
    
    @Version
    private long version;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @LastModifiedDate
    private Date updateDate;
    
    /**
     * {@link User} fields validation rules.
     */
    public static class Validation {
        public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Z0-9_\\-\\u0020]+$");
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 24;
        public static final int PASSWORD_MIN_LENGTH = 5;
        public static final int PASSWORD_MAX_LENGTH = 256;
    }
}
