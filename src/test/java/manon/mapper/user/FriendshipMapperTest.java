package manon.mapper.user;

import manon.document.user.FriendshipRequestEntity;
import manon.document.user.UserEntity;
import manon.dto.user.FriendshipRequestDto;
import manon.util.Tools;
import manon.util.mapper.UserMapperDataset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FriendshipMapperTest {

    static Object[][] dataProvider_toFriendshipRequestDto() {

        //<editor-fold defaultstate="collapsed" desc="dataset">
        UserEntity userEntityFrom1 = UserMapperDataset.toUserEntity(1);
        UserEntity userEntityTo1 = UserMapperDataset.toUserEntity(2);
        FriendshipRequestEntity from1 = FriendshipRequestEntity.builder()
            .id(10)
            .requestFrom(userEntityFrom1)
            .requestTo(userEntityTo1)
            .creationDate(Tools.now())
            .build();

        UserEntity userEntityFrom2 = UserMapperDataset.toUserEntity(3);
        UserEntity userEntityTo2 = UserMapperDataset.toUserEntity(4);
        FriendshipRequestEntity from2 = FriendshipRequestEntity.builder()
            .id(20)
            .requestFrom(userEntityFrom2)
            .requestTo(userEntityTo2)
            .creationDate(Tools.now())
            .build();

        List<FriendshipRequestEntity> from = Arrays.asList(from1, from2);

        FriendshipRequestDto to1 = new FriendshipRequestDto();
        to1.setId(from1.getId());
        to1.setRequestFrom(UserMapperDataset.toUserPublicInfo(userEntityFrom1));
        to1.setRequestTo(UserMapperDataset.toUserPublicInfo(userEntityTo1));
        to1.setCreationDate(from1.getCreationDate());

        FriendshipRequestDto to2 = new FriendshipRequestDto();
        to2.setId(from2.getId());
        to2.setRequestFrom(UserMapperDataset.toUserPublicInfo(userEntityFrom2));
        to2.setRequestTo(UserMapperDataset.toUserPublicInfo(userEntityTo2));
        to2.setCreationDate(from2.getCreationDate());

        List<FriendshipRequestDto> to = Arrays.asList(to1, to2);
        //</editor-fold>

        return new Object[][]{
            {null, null},
            {Collections.singletonList(FriendshipRequestEntity.builder().build()), Collections.singletonList(new FriendshipRequestDto())},
            {from, to}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProvider_toFriendshipRequestDto")
    void shouldVerify_toUserPublicInfo(List<FriendshipRequestEntity> from, List<FriendshipRequestDto> to) {
        assertThat(FriendshipMapper.MAPPER.toFriendshipRequestDto(from))
            .isEqualTo(to);
    }
}
