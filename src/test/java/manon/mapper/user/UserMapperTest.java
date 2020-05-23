package manon.mapper.user;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserDto;
import manon.dto.user.UserSnapshotDto;
import manon.dto.user.UserWithSnapshotsDto;
import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    static Object[][] dataProviderShouldMapUserToUserWithSnapshotsDto() {

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

        UserDto userDto = new UserDto();
        userDto.setId(nestedUser.getId());

        UserSnapshotDto userSnapshotDto1 = new UserSnapshotDto();
        userSnapshotDto1.setId(userSnapshot1.getId());
        userSnapshotDto1.setUser(userDto);
        userSnapshotDto1.setUserUsername(userSnapshot1.getUserUsername());
        userSnapshotDto1.setUserAuthorities(userSnapshot1.getUserAuthorities());
        userSnapshotDto1.setUserRegistrationState(userSnapshot1.getUserRegistrationState());
        userSnapshotDto1.setUserNickname(userSnapshot1.getUserNickname());
        userSnapshotDto1.setUserEmail(userSnapshot1.getUserEmail());
        userSnapshotDto1.setCreationDate(userSnapshot1.getCreationDate());

        UserSnapshotDto userSnapshotDto2 = new UserSnapshotDto();
        userSnapshotDto2.setId(userSnapshot2.getId());
        userSnapshotDto2.setUser(userDto);
        userSnapshotDto2.setUserUsername(userSnapshot2.getUserUsername());
        userSnapshotDto2.setUserAuthorities(userSnapshot2.getUserAuthorities());
        userSnapshotDto2.setUserRegistrationState(userSnapshot2.getUserRegistrationState());
        userSnapshotDto2.setUserNickname(userSnapshot2.getUserNickname());
        userSnapshotDto2.setUserEmail(userSnapshot2.getUserEmail());
        userSnapshotDto2.setCreationDate(userSnapshot2.getCreationDate());

        UserWithSnapshotsDto userWithSnapshotsDto = new UserWithSnapshotsDto();
        userWithSnapshotsDto.setId(user.getId());
        userWithSnapshotsDto.setUsername(user.getUsername());
        userWithSnapshotsDto.setAuthorities(user.getAuthorities());
        userWithSnapshotsDto.setRegistrationState(user.getRegistrationState());
        userWithSnapshotsDto.setNickname(user.getNickname());
        userWithSnapshotsDto.setEmail(user.getEmail());
        userWithSnapshotsDto.setCreationDate(user.getCreationDate());
        userWithSnapshotsDto.setUpdateDate(user.getUpdateDate());
        userWithSnapshotsDto.setUserSnapshots(Arrays.asList(
            userSnapshotDto1,
            userSnapshotDto2
        ));
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {new UserEntity(), new UserWithSnapshotsDto()},
            {user, userWithSnapshotsDto}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldMapUserToUserWithSnapshotsDto")
    void shouldMapUserToUserWithSnapshotsDto(UserEntity user, UserWithSnapshotsDto userWithSnapshotsDto) {
        assertThat(UserMapper.MAPPER.userToUserWithSnapshotsDto(user))
            .isEqualTo(userWithSnapshotsDto);
    }
}
