package manon.mapper.user;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserDto;
import manon.dto.user.UserSnapshotDto;
import manon.dto.user.UserWithSnapshotsDto;
import manon.model.user.UserPublicInfo;
import manon.util.mapper.UserMapperDataset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    static Object[][] dataProvider_shouldVerify_toUserWithSnapshotsDto() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity nestedUser = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot1 = UserMapperDataset.toUserSnapshotEntity(1, nestedUser);
        UserSnapshotEntity userSnapshot2 = UserMapperDataset.toUserSnapshotEntity(2, nestedUser);
        UserEntity user = UserMapperDataset.toUserEntity(1, userSnapshot1, userSnapshot2);

        UserDto userDto = UserMapperDataset.toUserDto(nestedUser);
        UserSnapshotDto userSnapshotDto1 = UserMapperDataset.toUserSnapshotDto(userDto, userSnapshot1);
        UserSnapshotDto userSnapshotDto2 = UserMapperDataset.toUserSnapshotDto(userDto, userSnapshot2);
        UserWithSnapshotsDto userWithSnapshotsDto = UserMapperDataset.toUserWithSnapshotsDto(userDto, userSnapshotDto1, userSnapshotDto2);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {new UserEntity(), new UserWithSnapshotsDto()},
            {user, userWithSnapshotsDto}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_shouldVerify_toUserWithSnapshotsDto")
    void shouldVerify_toUserWithSnapshotsDto(UserEntity from, UserWithSnapshotsDto to) {
        assertThat(UserMapper.MAPPER.toUserWithSnapshotsDto(from))
            .isEqualTo(to);
    }

    static Object[][] dataProvider_shouldVerify_toUserSnapshotEntity() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity nestedUser = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot1 = UserMapperDataset.toUserSnapshotEntity(1, nestedUser);
        UserSnapshotEntity userSnapshot2 = UserMapperDataset.toUserSnapshotEntity(2, nestedUser);
        UserEntity user = UserMapperDataset.toUserEntity(1, userSnapshot1, userSnapshot2);

        UserSnapshotEntity userSnapshotEntity = UserMapperDataset.toUserSnapshotEntity(nestedUser);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {new UserEntity(), UserSnapshotEntity.builder().user(new UserEntity()).build()},
            {user, userSnapshotEntity}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_shouldVerify_toUserSnapshotEntity")
    void shouldVerify_toUserSnapshotEntity(UserEntity from, UserSnapshotEntity to) {
        UserSnapshotEntity mapped = UserMapper.MAPPER.toUserSnapshotEntity(from);
        assertThat(mapped).isEqualTo(to);
        if (from != null && to != null) {
            assertThat(mapped.getUser()).isEqualTo(to.getUser());
        }
    }

    static Object[][] dataProvider_shouldVerify_toUserDto() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity userEntity = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot1 = UserMapperDataset.toUserSnapshotEntity(1, userEntity);
        UserSnapshotEntity userSnapshot2 = UserMapperDataset.toUserSnapshotEntity(2, userEntity);
        UserEntity user = UserMapperDataset.toUserEntity(1, userSnapshot1, userSnapshot2);

        UserDto userDto = UserMapperDataset.toUserDto(userEntity);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {new UserEntity(), new UserDto()},
            {user, userDto}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_shouldVerify_toUserDto")
    void shouldVerify_toUserDto(UserEntity from, UserDto to) {
        assertThat(UserMapper.MAPPER.toUserDto(from))
            .isEqualTo(to);
    }

    static Object[][] dataProvider_shouldVerify_toUserDtoList() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity nestedUserEntity1 = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot11 = UserMapperDataset.toUserSnapshotEntity(1, nestedUserEntity1);
        UserSnapshotEntity userSnapshot12 = UserMapperDataset.toUserSnapshotEntity(2, nestedUserEntity1);
        UserEntity userEntity1 = UserMapperDataset.toUserEntity(1, userSnapshot11, userSnapshot12);

        UserEntity nestedUserEntity2 = UserMapperDataset.toUserEntity(2);
        UserSnapshotEntity userSnapshot21 = UserMapperDataset.toUserSnapshotEntity(3, nestedUserEntity2);
        UserSnapshotEntity userSnapshot22 = UserMapperDataset.toUserSnapshotEntity(4, nestedUserEntity2);
        UserEntity userEntity2 = UserMapperDataset.toUserEntity(2, userSnapshot21, userSnapshot22);

        List<UserEntity> userEntities = Arrays.asList(userEntity1, userEntity2);

        UserDto userDto1 = UserMapperDataset.toUserDto(nestedUserEntity1);
        UserDto userDto2 = UserMapperDataset.toUserDto(nestedUserEntity2);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {Collections.singletonList(new UserEntity()), Collections.singletonList(new UserDto())},
            {userEntities, userDtos}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_shouldVerify_toUserDtoList")
    void shouldVerify_toUserDtoList(List<UserEntity> from, List<UserDto> to) {
        assertThat(UserMapper.MAPPER.toUserDtoList(from))
            .isEqualTo(to);
    }

    static Object[][] dataProvider_shouldVerify_toUserDtoPage() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity nestedUserEntity1 = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot11 = UserMapperDataset.toUserSnapshotEntity(1, nestedUserEntity1);
        UserSnapshotEntity userSnapshot12 = UserMapperDataset.toUserSnapshotEntity(2, nestedUserEntity1);
        UserEntity userEntity1 = UserMapperDataset.toUserEntity(1, userSnapshot11, userSnapshot12);

        UserEntity nestedUserEntity2 = UserMapperDataset.toUserEntity(2);
        UserSnapshotEntity userSnapshot21 = UserMapperDataset.toUserSnapshotEntity(3, nestedUserEntity2);
        UserSnapshotEntity userSnapshot22 = UserMapperDataset.toUserSnapshotEntity(4, nestedUserEntity2);
        UserEntity userEntity2 = UserMapperDataset.toUserEntity(2, userSnapshot21, userSnapshot22);

        Pageable pageable = Pageable.unpaged();

        List<UserEntity> userEntities = Arrays.asList(userEntity1, userEntity2);
        PageImpl<UserEntity> userEntitiesPage = new PageImpl<>(userEntities, pageable, userEntities.size());

        UserDto userDto1 = UserMapperDataset.toUserDto(nestedUserEntity1);
        UserDto userDto2 = UserMapperDataset.toUserDto(nestedUserEntity2);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);
        PageImpl<UserDto> userDtosPage = new PageImpl<>(userDtos, pageable, userDtos.size());

        PageImpl<UserEntity> emptyUserEntitiesPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        PageImpl<UserDto> emptyUserDtosPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        //</editor-fold>

        return new Object[][]{
            {null, pageable, null},
            {emptyUserEntitiesPage, pageable, emptyUserDtosPage},
            {userEntitiesPage, pageable, userDtosPage}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_shouldVerify_toUserDtoPage")
    void shouldVerify_toUserDtoPage(Page<UserEntity> from, Pageable pageable, Page<UserDto> to) {
        assertThat(UserMapper.MAPPER.toUserDtoPage(from, pageable))
            .isEqualTo(to);
    }

    static Object[][] dataProvider_toUserPublicInfo() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity nestedUserEntity1 = UserMapperDataset.toUserEntity(1);
        UserSnapshotEntity userSnapshot11 = UserMapperDataset.toUserSnapshotEntity(1, nestedUserEntity1);
        UserSnapshotEntity userSnapshot12 = UserMapperDataset.toUserSnapshotEntity(2, nestedUserEntity1);
        UserEntity userEntity = UserMapperDataset.toUserEntity(1, userSnapshot11, userSnapshot12);

        UserPublicInfo userPublicInfo = UserMapperDataset.toUserPublicInfo(userEntity);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {userEntity, userPublicInfo}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_toUserPublicInfo")
    void shouldVerify_toUserPublicInfo(UserEntity from, UserPublicInfo to) {
        assertThat(UserMapper.MAPPER.toUserPublicInfo(from))
            .isEqualTo(to);
    }
}
