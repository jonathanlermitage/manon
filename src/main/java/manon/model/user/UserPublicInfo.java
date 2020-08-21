package manon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
}
