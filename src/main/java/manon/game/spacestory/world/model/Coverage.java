package manon.game.spacestory.world.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class Coverage {
    
    private long topRightX;
    private long topRightY;
    private long bottomLeftX;
    private long bottomLeftY;
}
