package manon.game.spacestory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Strings {
    
    /** {@code WORLD_NAME_number}. */
    WORLD_NAME("WORLD_NAME_%d"),
    
    /** {@code SECTOR_NAME_x_y}. */
    SECTOR_NAME("SECTOR_NAME_%d_%d"),
    
    /** {@code POINT_NAME_x_y}. */
    POINT_NAME("POINT_NAME_%d_%d");
    
    /** {@code String.format} pattern. */
    String pattern;
    
    public static String key(Strings value, Object... params) {
        return String.format(value.getPattern(), params);
    }
}
