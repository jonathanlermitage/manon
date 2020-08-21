package manon.mapper.user;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserDto;
import manon.dto.user.UserWithSnapshotsDto;
import manon.model.user.UserPublicInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    public static final UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    public abstract UserWithSnapshotsDto toUserWithSnapshotsDto(UserEntity from);

    /** Copy {@link UserEntity} to {@link UserSnapshotEntity} ; nota: <b>returned entity has
     * no {@code id} and no {@code creationDate}</b>. */
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "creationDate", target = "creationDate", ignore = true)
    @Mapping(source = "username", target = "userUsername")
    @Mapping(source = "authorities", target = "userAuthorities")
    @Mapping(source = "password", target = "userPassword")
    @Mapping(source = "registrationState", target = "userRegistrationState")
    @Mapping(source = "nickname", target = "userNickname")
    @Mapping(source = "email", target = "userEmail")
    @Mapping(source = "version", target = "userVersion")
    @Mapping(source = "from", target = "user")
    public abstract UserSnapshotEntity toUserSnapshotEntity(UserEntity from);

    public abstract UserDto toUserDto(UserEntity from);

    public abstract List<UserDto> toUserDtoList(List<UserEntity> from);

    public Page<UserDto> toUserDtoPage(Page<UserEntity> userEntities, Pageable pageable) {
        if (userEntities == null) {
            return null;
        }
        List<UserEntity> entitiesList = userEntities.get().collect(Collectors.toList());
        List<UserDto> dtosList = UserMapper.MAPPER.toUserDtoList(entitiesList);
        return new PageImpl<>(dtosList, pageable, userEntities.getTotalElements());
    }

    public abstract UserPublicInfo toUserPublicInfo(UserEntity from);
}
