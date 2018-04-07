package manon.game.world.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WorldKey {
    
    /** {@code SECTOR_NAME_x_y}. */
    SECTOR_NAME("SECTOR_NAME_%d_%d"),
    
    /** {@code POINT_NAME_x_y}. */
    POINT_NAME("POINT_NAME_%d_%d");
    
    /** {@code String.format} pattern. */
    String pattern;
    
    public static String key(WorldKey value, Object... params) {
        return String.format(value.getPattern(), params);
    }
}
