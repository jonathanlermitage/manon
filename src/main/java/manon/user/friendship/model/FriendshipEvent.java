package manon.user.friendship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.now;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class FriendshipEvent implements Serializable {
    
    @Builder.Default
    private Date date = now();
    private Code event;
    private List<Object> params;
    
    public enum Code {
        YOU_SENT_FRIEND_REQUEST,
        YOU_ACCEPTED_FRIEND_REQUEST,
        YOU_REJECTED_FRIEND_REQUEST,
        YOU_CANCELED_FRIEND_REQUEST,
        YOU_REVOKED_FRIEND_REQUEST,
        
        TARGET_SENT_FRIEND_REQUEST,
        TARGET_ACCEPTED_FRIEND_REQUEST,
        TARGET_REJECTED_FRIEND_REQUEST,
        TARGET_CANCELED_FRIEND_REQUEST,
        TARGET_REVOKED_FRIEND_REQUEST
    }
}
