package manon.user.model;

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
    
    private static final long serialVersionUID = -7424248970085002709L;
    
    @Builder.Default
    private Date date = now();
    private FriendshipEventCode code;
    private List<? extends Serializable> params;
}
