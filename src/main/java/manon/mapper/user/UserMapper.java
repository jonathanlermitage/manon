package manon.mapper.user;

import manon.document.user.UserEntity;
import manon.dto.user.UserWithSnapshotsDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    public static final UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    public abstract UserWithSnapshotsDto userToUserWithSnapshotsDto(UserEntity from);
}
