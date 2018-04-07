package manon.game.world.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.game.world.document.WorldSummaryProjection;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/** World simple fields projection. */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class WorldSummary implements Serializable, WorldSummaryProjection {
    
    private static final long serialVersionUID = -8359991883855053207L;
    
    String id;
    String name;
    int nbSectors;
    int nbPoints;
}
