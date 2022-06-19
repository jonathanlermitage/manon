package manon.api.user;

import io.restassured.response.Response;
import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserDto;
import manon.dto.user.UserWithSnapshotsDto;
import manon.err.user.UserExistsException;
import manon.mapper.user.UserMapper;
import manon.model.user.UserPublicInfo;
import manon.model.user.form.UserLogin;
import manon.model.user.form.UserPasswordUpdateForm;
import manon.model.user.form.UserUpdateForm;
import manon.util.TestTools;
import manon.util.basetest.AbstractIT;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static manon.model.user.RegistrationState.ACTIVE;
import static manon.model.user.RegistrationState.DELETED;
import static manon.model.user.UserRole.PLAYER;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;

class UserWSIT extends AbstractIT {

    Object[][] dataProviderShouldRegister() {
        return new Object[][]{
            {"JOHN", "12300"},
            {"BOB DYLAN AND FRIENDS", "PASSWORD"},
            {"A B CX_Y-Z", "secret  p4ssword!"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldRegister")
    void shouldRegister(String name, String pwd) {
        whenAnonymous().getSpec()
            .body(UserLogin.builder().username(name).password(pwd).build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        UserEntity user = userService.readByUsername(name);
        assertThat(user.getUsername()).isEqualTo(name);
        assertThat(user.getAuthorities()).isEqualTo(PLAYER.getAuthority());
        assertThat(user.getVersion()).isGreaterThanOrEqualTo(0L);
        assertThat(user.getRegistrationState()).isEqualTo(ACTIVE);
        assertThat(pwd).as("don't store raw passwords!")
            .isNotEqualTo(user.getPassword())
            .doesNotContain(user.getPassword());
    }

    @Test
    void shouldNotRegisterTwice() {
        whenAnonymous().getSpec()
            .body(UserLogin.builder().username("DUPLICATE").password("12300").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        whenAnonymous().getSpec()
            .body(UserLogin.builder().username("DUPLICATE").password("456789").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CONFLICT).contentType(JSON)
            .body(MANAGED_ERROR_TYPE, Matchers.equalTo(UserExistsException.class.getSimpleName()));
    }

    @Test
    void shouldDeleteAndLooseAuthorisations() {
        whenP1().getSpec()
            .get(API_USER).then()
            .statusCode(SC_OK);
        whenP1().getSpec()
            .delete(API_USER).then()
            .statusCode(SC_OK);
        assertThat(userService.readOne(userId(1)).getRegistrationState()).isEqualTo(DELETED);
        login(name(1), pwd(1))
            .then().statusCode(SC_UNAUTHORIZED);
    }

    @Test
    void shouldRead() {
        Response res = whenP1().getSpec()
            .get(API_USER);
        res.then()
            .statusCode(SC_OK);
        UserDto webUser = readValue(res, UserDto.class);
        UserEntity dbUser = userService.readOneAndFetchUserSnapshots(userId(1)).toBuilder().password(null).build();
        assertThat(webUser).isEqualTo(UserMapper.MAPPER.toUserDto(dbUser));
    }

    @Test
    void shouldReadAndNotIncludeUserSnapshots() {
        userSnapshotService.persistAll(Arrays.asList(
            UserSnapshotEntity.builder().user(user(1)).userUsername("u1").userNickname("x1").build(),
            UserSnapshotEntity.builder().user(user(1)).userUsername("u1").userNickname("y1").build()
        ));
        Response res = whenP1().getSpec()
            .get(API_USER);
        res.then()
            .statusCode(SC_OK);
        UserDto webUser = readValue(res, UserDto.class);
        UserEntity dbUser = userService.readOneAndFetchUserSnapshots(userId(1)).toBuilder().password(null).build();
        assertThat(webUser).isEqualTo(UserMapper.MAPPER.toUserDto(dbUser));
    }

    @Test
    void shouldReadAndIncludeUserSnapshots() {
        Response res = whenP1().getSpec()
            .get(API_USER + "/include/usersnapshots");
        res.then()
            .statusCode(SC_OK);
        UserWithSnapshotsDto webUser = readValue(res, UserWithSnapshotsDto.class);
        UserWithSnapshotsDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        assertThat(webUser).isEqualTo(dbUser);
        assertThat(webUser.getUserSnapshots()).isEmpty();
    }

    @Test
    void shouldReadAndIncludeUserSnapshotsWhenUserHasSnapshots() {
        userSnapshotService.persistAll(Arrays.asList(
            UserSnapshotEntity.builder().user(user(1)).userUsername("u1").userNickname("x1").build(),
            UserSnapshotEntity.builder().user(user(1)).userUsername("u1").userNickname("y1").build()
        ));
        Response res = whenP1().getSpec()
            .get(API_USER + "/include/usersnapshots");
        res.then()
            .statusCode(SC_OK);
        UserWithSnapshotsDto webUser = readValue(res, UserWithSnapshotsDto.class);
        UserWithSnapshotsDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        assertThat(webUser).isEqualTo(dbUser);
        assertThat(webUser.getUserSnapshots()).hasSize(2).isEqualTo(dbUser.getUserSnapshots());
    }

    @Test
    void shouldReadVersion() {
        Response res = whenP1().getSpec()
            .get(API_USER + "/version");
        res.then()
            .statusCode(SC_OK);
        Long webUserVersion = readValue(res, Long.class);
        Long dbUserVersion = userService.readOne(userId(1)).getVersion();
        assertThat(webUserVersion).isEqualTo(dbUserVersion);
    }

    Object[][] dataProviderShouldUpdate() {
        return new Object[][]{
            {"", ""},
            {"nickname", "test.foo@bar.com"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldUpdate")
    void shouldUpdate(String nickname, String email) {
        UserEntity userBefore = userService.readOne(userId(1));
        whenP1().getSpec()
            .body(UserUpdateForm.builder().nickname(nickname).email(email).build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(SC_OK);
        UserEntity userAfter = userService.readOne(userId(1));
        UserEntity userExpected = userBefore.toBuilder()
            .email(email)
            .nickname(nickname)
            .version(userBefore.getVersion() + 1)
            .build();
        assertThat(userAfter).isEqualTo(userExpected);
    }

    @Test
    void shouldUpdatePassword() {
        whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword(pwd(1)).newPassword("a new password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_OK);

        String token = loginAndReturnToken(name(1), "a new password");
        usingToken(token).get(API_USER)
            .then().statusCode(SC_OK);
    }

    @Test
    void shouldUpdateWithLongestPassword() {
        String newPassword = TestTools.fill("anewpassword", UserEntity.Validation.PASSWORD_MAX_LENGTH);
        whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword(pwd(1)).newPassword(newPassword).build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_OK);

        String token = loginAndReturnToken(name(1), newPassword);
        usingToken(token).get(API_USER)
            .then().statusCode(SC_OK);
    }

    @Test
    void shouldVerifyPasswordWithDataLongerThanBCryptMaxLength() {
        // BCrypt truncates too long password. See https://security.stackexchange.com/questions/39849/does-bcrypt-have-a-maximum-password-length
        String newPassword = TestTools.fill("anewpassword", UserEntity.Validation.PASSWORD_MAX_LENGTH);
        whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword(pwd(1)).newPassword(newPassword).build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_OK);

        login(name(1), newPassword.substring(0, 80))
            .then().statusCode(SC_OK);
    }

    @Test
    void shouldNotVerifyPasswordWithDataShorterThanBCryptMaxLength() {
        // BCrypt truncates too long passwords. See https://security.stackexchange.com/questions/39849/does-bcrypt-have-a-maximum-password-length
        String newPassword = TestTools.fill("anewpassword", UserEntity.Validation.PASSWORD_MAX_LENGTH);
        whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword(pwd(1)).newPassword(newPassword).build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_OK);

        login(name(1), newPassword.substring(0, 20))
            .then().statusCode(SC_UNAUTHORIZED);
    }

    @Test
    void shouldNotUpdateBadPassword() {
        whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword("wrongpassword").newPassword("a new password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_BAD_REQUEST);

        login(name(1), "a new password")
            .then().statusCode(SC_UNAUTHORIZED);
        login(name(1), pwd(1))
            .then().statusCode(SC_OK);
    }

    @Test
    void shouldGetZeroFriends() {
        //GIVEN a user with no friend

        //WHEN he gets friends
        Response res = whenP1().getSpec()
            .get(API_USER + "/friends");
        res.then()
            .statusCode(SC_OK);
        List<UserPublicInfo> friends = Arrays.asList(res.getBody().as(UserPublicInfo[].class));

        //THEN he gets nothing
        assertThat(friends).isEmpty();
    }
}
