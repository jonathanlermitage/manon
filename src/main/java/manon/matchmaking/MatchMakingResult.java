package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

/**
 * Result of matchmaking parking process.
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class MatchMakingResult {
    
    /** Process duration in ms. */
    private long duration;
    
    /** Number of pending profiles that have been parked into a game. */
    private int playersParked;
    
    /** Number of pending profiles that have not been parked into a game. */
    private int playersNotParked;
}
