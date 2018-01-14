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
import manon.user.friendship.FriendshipEvent;
import manon.user.registration.RegistrationStateEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static lombok.AccessLevel.PRIVATE;
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
public final class User implements Serializable {
    
    @Id
    private String id;
    
    /** Unique, non case-sensitive and not modifiable login name. */
    @Indexed(background = true, unique = true)
    private String username;
    
    private List<UserAuthority> roles;
    
    @JsonIgnore
    private String password;
    
    private RegistrationStateEnum registrationState;
    
    /** A non-unique name that can change. */
    @Indexed(background = true)
    private String nickname;
    
    /** Email, NOT mandatory. */
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
        public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Z0-9_\\-\\u0020]+$");
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 24;
        
        public static final Pattern NICKNAME_PATTERN = compile("^[A-Z0-9_\\-\\u0020]+$", CASE_INSENSITIVE);
        public static final int NICKNAME_MAX_LENGTH = 24;
        
        public static final Pattern EMAIL_PATTERN = compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);
        public static final int EMAIL_MAX_LENGTH = 256;
        
        public static final int PASSWORD_MIN_LENGTH = 5;
        public static final int PASSWORD_MAX_LENGTH = 256;
        
        public static final int MAX_EVENTS = 30;
    }
}
