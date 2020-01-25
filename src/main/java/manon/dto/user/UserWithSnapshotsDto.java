package manon.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithSnapshotsDto extends AbstractUserDto {

    private List<UserSnapshotDto> userSnapshots;
}
