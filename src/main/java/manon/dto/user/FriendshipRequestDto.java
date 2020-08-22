package manon.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import manon.model.user.UserPublicInfo;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class FriendshipRequestDto {

    private long id;

    private UserPublicInfo requestFrom;

    private UserPublicInfo requestTo;

    private LocalDateTime creationDate;
}
