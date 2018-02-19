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
import manon.user.friendship.model.FriendshipEvent;
import manon.user.registration.RegistrationStateEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static manon.user.document.User.Validation.EMAIL_MAX_LENGTH;
import static manon.user.document.User.Validation.EMAIL_SIZE_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_MAX_LENGTH;
import static manon.user.document.User.Validation.NICKNAME_PATTERN;
import static manon.user.document.User.Validation.NICKNAME_PATTERN_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_SIZE_ERRMSG;
import static manon.user.document.User.Validation.PASSWORD_MAX_LENGTH;
import static manon.user.document.User.Validation.PASSWORD_MIN_LENGTH;
import static manon.user.document.User.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.user.document.User.Validation.USERNAME_MAX_LENGTH;
import static manon.user.document.User.Validation.USERNAME_MIN_LENGTH;
import static manon.user.document.User.Validation.USERNAME_PATTERN;
import static manon.user.document.User.Validation.USERNAME_PATTERN_ERRMSG;
import static manon.user.document.User.Validation.USERNAME_SIZE_ERRMSG;
import static manon.util.Tools.DATE_FORMAT;

/**
 * User user.
 */
@Document(collection = "User")
@TypeAlias("User")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class User implements Serializable, UserVersion {
    
    @Id
    private String id;
    
    /** Unique, uppercase and not modifiable login name. */
    @Indexed(background = true, unique = true)
    @NotNull(message = USERNAME_SIZE_ERRMSG)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = USERNAME_SIZE_ERRMSG)
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_PATTERN_ERRMSG)
    private String username;
    
    private List<UserAuthority> roles;
    
    @JsonIgnore
    @NotNull(message = PASSWORD_SIZE_ERRMSG)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_SIZE_ERRMSG)
    private String password;
    
    private RegistrationStateEnum registrationState;
    
    /** A non-unique name that can change, NOT mandatory. */
    @Indexed(background = true)
    @Size(max = NICKNAME_MAX_LENGTH, message = NICKNAME_SIZE_ERRMSG)
    @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_PATTERN_ERRMSG)
    private String nickname;
    
    /** Email, NOT mandatory. */
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_SIZE_ERRMSG)
    private String email;
    
    // social
    @Builder.Default
    private List<String> friendshipRequestsTo = new ArrayList<>();
    @Builder.Default
    private List<String> friendshipRequestsFrom = new ArrayList<>();
    @Builder.Default
    private List<String> friends = new ArrayList<>();
    @Builder.Default
    private List<FriendshipEvent> friendshipEvents = new ArrayList<>();
    
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
        
        public static final int PASSWORD_MIN_LENGTH = 5;
        public static final int PASSWORD_MAX_LENGTH = 256;
        public static final String PASSWORD_SIZE_ERRMSG = "PASSWORD_SIZE";
        
        public static final int MAX_EVENTS = 30;
    }
}
