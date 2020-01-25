package manon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.document.user.UserEntity;

/** User projection that shows fields visible to public. */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
@Builder(toBuilder = true)
public class UserPublicInfo {

    private long id;
    private String username;
    private String nickname;

    /** Populate a {@link UserPublicInfo} from a {@link UserEntity}. */
    public static UserPublicInfo from(UserEntity user) {
        return UserPublicInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .build();
    }
}
