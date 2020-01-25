package manon.mapper.user;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserResponseDto;
import manon.dto.user.UserSnapshotResponseDto;
import manon.dto.user.UserWithSnapshotsResponseDto;
import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    public static Object[][] dataProviderShouldMapUserToUserWithSnapshotsResponseDto() {

        //<editor-fold defaultstate="collapsed" desc="user with data">
        UserEntity nestedUser = UserEntity.builder().id(1).build();

        UserSnapshotEntity userSnapshot1 = UserSnapshotEntity.builder()
            .id(1)
            .user(nestedUser)
            .userUsername("u1")
            .userAuthorities(UserRole.PLAYER.name())
            .userPassword("apassword1")
            .userRegistrationState(RegistrationState.ACTIVE)
            .userNickname("n1")
            .userEmail("e1")
            .userVersion(1)
            .creationDate(Tools.now())
            .build();

        UserSnapshotEntity userSnapshot2 = UserSnapshotEntity.builder()
            .id(1)
            .user(nestedUser)
            .userUsername("u2")
            .userAuthorities(UserRole.PLAYER.name())
            .userPassword("apassword2")
            .userRegistrationState(RegistrationState.SUSPENDED)
            .userNickname("n2")
            .userEmail("e2")
            .userVersion(2)
            .creationDate(Tools.now())
            .build();

        UserEntity user = UserEntity.builder()
            .id(1)
            .username("u")
            .authorities(UserRole.PLAYER.name())
            .password("apassword")
            .registrationState(RegistrationState.ACTIVE)
            .nickname("n")
            .email("e")
            .version(3)
            .creationDate(Tools.now())
            .updateDate(Tools.now())
            .userSnapshots(Arrays.asList(
                userSnapshot1,
                userSnapshot2
            ))
            .build();

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(nestedUser.getId());

        UserSnapshotResponseDto userSnapshotResponsetDto1 = new UserSnapshotResponseDto();
        userSnapshotResponsetDto1.setId(userSnapshot1.getId());
        userSnapshotResponsetDto1.setUser(userResponseDto);
        userSnapshotResponsetDto1.setUserUsername(userSnapshot1.getUserUsername());
        userSnapshotResponsetDto1.setUserAuthorities(userSnapshot1.getUserAuthorities());
        userSnapshotResponsetDto1.setUserRegistrationState(userSnapshot1.getUserRegistrationState());
        userSnapshotResponsetDto1.setUserNickname(userSnapshot1.getUserNickname());
        userSnapshotResponsetDto1.setUserEmail(userSnapshot1.getUserEmail());
        userSnapshotResponsetDto1.setCreationDate(userSnapshot1.getCreationDate());

        UserSnapshotResponseDto userSnapshotResponsetDto2 = new UserSnapshotResponseDto();
        userSnapshotResponsetDto2.setId(userSnapshot2.getId());
        userSnapshotResponsetDto2.setUser(userResponseDto);
        userSnapshotResponsetDto2.setUserUsername(userSnapshot2.getUserUsername());
        userSnapshotResponsetDto2.setUserAuthorities(userSnapshot2.getUserAuthorities());
        userSnapshotResponsetDto2.setUserRegistrationState(userSnapshot2.getUserRegistrationState());
        userSnapshotResponsetDto2.setUserNickname(userSnapshot2.getUserNickname());
        userSnapshotResponsetDto2.setUserEmail(userSnapshot2.getUserEmail());
        userSnapshotResponsetDto2.setCreationDate(userSnapshot2.getCreationDate());

        UserWithSnapshotsResponseDto userWithSnapshotsResponseDto = new UserWithSnapshotsResponseDto();
        userWithSnapshotsResponseDto.setId(user.getId());
        userWithSnapshotsResponseDto.setUsername(user.getUsername());
        userWithSnapshotsResponseDto.setAuthorities(user.getAuthorities());
        userWithSnapshotsResponseDto.setRegistrationState(user.getRegistrationState());
        userWithSnapshotsResponseDto.setNickname(user.getNickname());
        userWithSnapshotsResponseDto.setEmail(user.getEmail());
        userWithSnapshotsResponseDto.setCreationDate(user.getCreationDate());
        userWithSnapshotsResponseDto.setUpdateDate(user.getUpdateDate());
        userWithSnapshotsResponseDto.setUserSnapshots(Arrays.asList(
            userSnapshotResponsetDto1,
            userSnapshotResponsetDto2
        ));
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {new UserEntity(), new UserWithSnapshotsResponseDto()},
            {user, userWithSnapshotsResponseDto}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldMapUserToUserWithSnapshotsResponseDto")
    public void shouldMapUserToUserWithSnapshotsResponseDto(UserEntity user, UserWithSnapshotsResponseDto userWithSnapshotsResponseDto) {
        assertThat(UserMapper.MAPPER.userToUserWithSnapshotsResponseDto(user))
            .isEqualTo(userWithSnapshotsResponseDto);
    }
}
