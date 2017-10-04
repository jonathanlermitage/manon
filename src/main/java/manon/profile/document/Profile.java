package manon.profile.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.profile.ProfileSharingOptionsEnum;
import manon.profile.friendship.FriendshipEvent;
import manon.user.document.User;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

/**
 * User profile.
 * For registration and authentication data, see {@link manon.user.document.User}
 */
@Document(collection = "Profile")
@TypeAlias("Profile")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class Profile implements Serializable {
    
    @Id
    private String id;
    
    /** This profile is compatible with codename <b>Manon</b> platform.*/
    @JsonIgnore
    private final String platformCompat = "manon";
    
    /** A non-unique name that can change. */
    @Indexed(background = true)
    private String nickname;
    
    /** A non-unique clan tag that can change. */
    private String clantag;
    
    /** A short description provided by the profile. */
    private String description;
    
    /** Email, NOT mandatory. */
    private String email;
    
    /** Level. */
    @Builder.Default
    private long level = 0;
    
    /** Skill, recomputed after each win or loss. */
    @Builder.Default
    private boolean cheater = false;
    
    // gaming TODO split to dedicated document?
    @JsonFormat(pattern = DATE_FORMAT)
    private Date lastGameStartDate;
    @JsonFormat(pattern = DATE_FORMAT)
    private Date lastGameActionDate;
    @Builder.Default
    private long skill = Default.INITIAL_SKILL;
    @Builder.Default
    private long coins = Default.INITIAL_COIN;
    @Builder.Default
    private long coinsSpent = 0;
    @Builder.Default
    private long moneySpent = 0;
    @Builder.Default
    private long nbFightWin = 0;
    @Builder.Default
    private long nbFightLost = 0;
    @Builder.Default
    private long nbFightEscape = 0;
    
    // social TODO split to dedicated document?
    @Builder.Default
    private Set<ProfileSharingOptionsEnum> sharingOptions = new HashSet<>();
    @Builder.Default
    private List<String> friendshipRequestsTo = new ArrayList<>();
    @Builder.Default
    private List<String> friendshipRequestsFrom = new ArrayList<>();
    @Builder.Default
    private List<String> friends = new ArrayList<>();
    
    // history TODO split to dedicated document?
    @Builder.Default
    private Map<Date, String> lastAlliedProfileIds = new HashMap<>();
    @Builder.Default
    private Map<Date, String> lastHostileProfileIds = new HashMap<>();
    @Builder.Default
    private Map<Date, String> lastHostileMonsterNames = new HashMap<>();
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
     * {@link Profile} fields validation rules.
     */
    public static class Validation {
        public static final Pattern CLANTAG_PATTERN = Pattern.compile("^[A-Z0-9_\\-\\u0020]+$", Pattern.CASE_INSENSITIVE);
        public static final Pattern NICKNAME_PATTERN = Pattern.compile("^[A-Z0-9_\\-\\u0020]+$", Pattern.CASE_INSENSITIVE);
        public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        public static final int MAX_EVENTS = 30;
        public static final int CLANTAG_MAX_LENGTH = User.Validation.USERNAME_MAX_LENGTH;
        public static final int NICKNAME_MAX_LENGTH = User.Validation.USERNAME_MAX_LENGTH;
        public static final int EMAIL_MAX_LENGTH = 256;
    }
    
    /**
     * {@link Profile} fields default or initial values.
     */
    public static class Default {
        public static final long INITIAL_SKILL = 2000;
        public static final long INITIAL_COIN = 500;
    }
}
