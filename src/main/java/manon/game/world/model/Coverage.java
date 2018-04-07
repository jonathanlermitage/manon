package manon.game.world.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class Coverage implements Serializable {
    
    private static final long serialVersionUID = -8957268524382675659L;
    
    private long topRightX;
    private long topRightY;
    private long bottomLeftX;
    private long bottomLeftY;
}
