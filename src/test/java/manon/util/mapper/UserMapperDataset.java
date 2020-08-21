package manon.util.mapper;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserDto;
import manon.dto.user.UserSnapshotDto;
import manon.dto.user.UserWithSnapshotsDto;
import manon.model.user.RegistrationState;
import manon.model.user.UserPublicInfo;
import manon.model.user.UserRole;
import manon.util.Tools;

import java.time.LocalDateTime;
import java.util.Arrays;

public class UserMapperDataset {

    private static final LocalDateTime now = Tools.now();

    public static UserEntity toUserEntity(int id, UserSnapshotEntity... userSnapshotEntityList) {
        return UserEntity.builder()
            .id(id)
            .username("U" + id)
            .authorities(UserRole.PLAYER.name())
            .password("apassword" + id)
            .registrationState(RegistrationState.ACTIVE)
            .nickname("nickname" + id)
            .email("u" + id + "@localhost.net")
            .version(1)
            .creationDate(now)
            .updateDate(now)
            .userSnapshots(Arrays.asList(userSnapshotEntityList))
            .build();
    }

    public static UserSnapshotEntity toUserSnapshotEntity(UserEntity user) {
        return UserSnapshotEntity.builder()
            .user(user)
            .userUsername(user.getUsername())
            .userAuthorities(user.getAuthorities())
            .userPassword(user.getPassword())
            .userRegistrationState(user.getRegistrationState())
            .userNickname(user.getNickname())
            .userEmail(user.getEmail())
            .userVersion(1)
            .creationDate(now)
            .build();
    }

    public static UserSnapshotEntity toUserSnapshotEntity(int id, UserEntity user) {
        return toUserSnapshotEntity(user).toBuilder().id(id).build();
    }

    public static UserDto toUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setAuthorities(user.getAuthorities());
        userDto.setRegistrationState(user.getRegistrationState());
        userDto.setNickname(user.getNickname());
        userDto.setEmail(user.getEmail());
        userDto.setCreationDate(user.getCreationDate());
        userDto.setUpdateDate(user.getUpdateDate());
        return userDto;
    }

    public static UserSnapshotDto toUserSnapshotDto(UserDto user, UserSnapshotEntity userSnapshot) {
        UserSnapshotDto userSnapshotDto = new UserSnapshotDto();
        userSnapshotDto.setId(userSnapshot.getId());
        userSnapshotDto.setUser(user);
        userSnapshotDto.setUserUsername(userSnapshot.getUserUsername());
        userSnapshotDto.setUserAuthorities(userSnapshot.getUserAuthorities());
        userSnapshotDto.setUserRegistrationState(userSnapshot.getUserRegistrationState());
        userSnapshotDto.setUserNickname(userSnapshot.getUserNickname());
        userSnapshotDto.setUserEmail(userSnapshot.getUserEmail());
        userSnapshotDto.setCreationDate(userSnapshot.getCreationDate());
        return userSnapshotDto;
    }

    public static UserWithSnapshotsDto toUserWithSnapshotsDto(UserDto user, UserSnapshotDto... userSnapshots) {
        UserWithSnapshotsDto userWithSnapshotsDto = new UserWithSnapshotsDto();
        userWithSnapshotsDto.setId(user.getId());
        userWithSnapshotsDto.setUsername(user.getUsername());
        userWithSnapshotsDto.setAuthorities(user.getAuthorities());
        userWithSnapshotsDto.setRegistrationState(user.getRegistrationState());
        userWithSnapshotsDto.setNickname(user.getNickname());
        userWithSnapshotsDto.setEmail(user.getEmail());
        userWithSnapshotsDto.setCreationDate(user.getCreationDate());
        userWithSnapshotsDto.setUpdateDate(user.getUpdateDate());
        userWithSnapshotsDto.setUserSnapshots(Arrays.asList(userSnapshots));
        return userWithSnapshotsDto;
    }

    public static UserPublicInfo toUserPublicInfo(UserEntity from) {
        return UserPublicInfo.builder()
            .id(from.getId())
            .username(from.getUsername())
            .nickname(from.getNickname())
            .build();
    }
}
