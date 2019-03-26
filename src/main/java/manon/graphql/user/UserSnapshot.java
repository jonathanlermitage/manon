package manon.graphql.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import manon.model.user.RegistrationState;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode(exclude = "creationDate")
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserSnapshot {
    
    private long id;
    private String username;
    private String authorities;
    private RegistrationState registrationState;
    private String nickname;
    private String email;
    private long version;
    private LocalDateTime creationDate;
    
    public static UserSnapshot from(@NotNull manon.document.user.UserSnapshot userSnapshot) {
        return UserSnapshot.builder()
            .id(userSnapshot.getId())
            .username(userSnapshot.getUserUsername())
            .authorities(userSnapshot.getUserAuthorities())
            .nickname(userSnapshot.getUserNickname())
            .email(userSnapshot.getUserEmail())
            .version(userSnapshot.getUserVersion())
            .creationDate(userSnapshot.getCreationDate())
            .build();
    }
    
    public static List<UserSnapshot> from(@NotNull List<manon.document.user.UserSnapshot> userSnapshotList) {
        return userSnapshotList.stream().map(UserSnapshot::from).collect(Collectors.toList());
    }
}
