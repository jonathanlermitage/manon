package manon.graphql.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import manon.model.user.RegistrationState;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    
    private long id;
    private String username;
    private String authorities;
    private RegistrationState registrationState;
    private String nickname;
    private String email;
    private long version;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    
    public static User from(@NotNull manon.document.user.User user) {
        return User.builder()
            .id(user.getId())
            .username(user.getUsername())
            .authorities(user.getAuthorities())
            .registrationState(user.getRegistrationState())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .version(user.getVersion())
            .creationDate(user.getCreationDate())
            .updateDate(user.getUpdateDate())
            .build();
    }
}
