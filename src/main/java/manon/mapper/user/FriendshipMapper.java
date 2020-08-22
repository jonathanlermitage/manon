package manon.mapper.user;

import manon.document.user.FriendshipRequestEntity;
import manon.dto.user.FriendshipRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class FriendshipMapper {

    public static final FriendshipMapper MAPPER = Mappers.getMapper(FriendshipMapper.class);

    public abstract List<FriendshipRequestDto> toFriendshipRequestDto(List<FriendshipRequestEntity> from);
}
