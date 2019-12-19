package manon.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithSnapshotsResponseDto extends AbstractUserDto {

    private List<UserSnapshotResponseDto> userSnapshots;
}
