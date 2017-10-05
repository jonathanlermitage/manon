package manon.profile.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.profile.ProfileFieldEnum;
import manon.profile.ProfileUpdateForm;
import manon.profile.document.Profile;
import manon.profile.service.ProfileService;
import manon.user.UserPasswordUpdateForm;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import manon.user.service.UserService;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static manon.app.config.ControllerAdvice.FIELD_ERRORS;
import static manon.app.config.ControllerAdvice.FIELD_MESSAGE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertEquals;

public class ProfileWSTest extends InitBeforeTest {
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected ProfileService profileService;
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldDeleteAndLooseAuthorisations(Rs rs) throws Exception {
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE).then()
                .statusCode(SC_OK);
        rs.getRequestSpecification()
                .delete(getApiV1() + TEST_API_PROFILE).then()
                .statusCode(SC_OK);
        assertEquals(userService.readByUsername(rs.getUsername()).getRegistrationState(), RegistrationStateEnum.DELETED);
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE).then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    /** Can't delete a deleted profile. */
    @Test(dataProvider = DP_RS_USERS)
    public void shouldNotDeleteTwice(Rs rs) throws Exception {
        rs.getRequestSpecification()
                .delete(getApiV1() + TEST_API_PROFILE).then()
                .statusCode(SC_OK);
        assertEquals(userService.readByUsername(rs.getUsername()).getRegistrationState(), RegistrationStateEnum.DELETED);
        rs.getRequestSpecification()
                .delete(getApiV1() + TEST_API_PROFILE).then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldNotDelete_anonymous() throws Exception {
        whenAnonymous().getRequestSpecification()
                .delete(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
    
    /** A profile should be able to retrieve its own profile data. */
    @Test(dataProvider = DP_RS_USERS)
    public void shouldRead(Rs rs) throws Exception {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE);
        res.then()
                .statusCode(SC_OK);
        Profile webProfil = readValue(res, Profile.class);
        User user = userService.readByUsername((rs.getUsername()));
        Profile profile = profileService.readOne(user.getProfileId());
        assertEquals(webProfil, profile);
        assertEquals(webProfil.getCreationDate(), profile.getCreationDate());
        assertEquals(webProfil.getUpdateDate(), profile.getUpdateDate());
        assertEquals(webProfil.getVersion(), profile.getVersion());
    }
    
    /** Anonymous user should not be able to read his (non-existing) profile. */
    @Test
    public void shouldNotRead_anonymous() throws Exception {
        whenAnonymous().getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldUpdateNickname(Rs rs) throws Exception {
        User userBefore = userService.readByUsername(rs.getUsername());
        Profile profileBefore = profileService.readOne(userBefore.getProfileId());
        rs.getRequestSpecification()
                .body(new ProfileUpdateForm(ProfileFieldEnum.NICKNAME, "a new nickname"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/field")
                .then()
                .statusCode(SC_OK);
        User userAfter = userService.readByUsername(rs.getUsername());
        Profile profileAfter = profileService.readOne(userBefore.getProfileId());
        profileBefore = profileBefore.toBuilder()
                .nickname("a new nickname")
                .version(profileBefore.getVersion() + 1)
                .build();
        assertEquals(userAfter, userBefore);
        assertEquals(profileAfter, profileBefore);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldUpdateClantag(Rs rs) throws Exception {
        User userBefore = userService.readByUsername(rs.getUsername());
        Profile profileBefore = profileService.readOne(userBefore.getProfileId());
        rs.getRequestSpecification()
                .body(new ProfileUpdateForm(ProfileFieldEnum.CLANTAG, "a new clantag"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/field")
                .then()
                .statusCode(SC_OK);
        User userAfter = userService.readByUsername(rs.getUsername());
        Profile profileAfter = profileService.readOne(userBefore.getProfileId());
        profileBefore = profileBefore.toBuilder()
                .clantag("a new clantag")
                .version(profileBefore.getVersion() + 1)
                .build();
        assertEquals(userAfter, userBefore);
        assertEquals(profileAfter, profileBefore);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldUpdateEmail(Rs rs) throws Exception {
        User userBefore = userService.readByUsername((rs.getUsername()));
        Profile profileBefore = profileService.readOne(userBefore.getProfileId());
        rs.getRequestSpecification()
                .body(new ProfileUpdateForm(ProfileFieldEnum.EMAIL, "test.foo@bar.com"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/field")
                .then()
                .statusCode(SC_OK);
        User userAfter = userService.readByUsername((rs.getUsername()));
        Profile profileAfter = profileService.readOne(userBefore.getProfileId());
        profileBefore = profileBefore.toBuilder()
                .email("test.foo@bar.com")
                .version(profileBefore.getVersion() + 1)
                .build();
        assertEquals(userAfter, userBefore);
        assertEquals(profileAfter, profileBefore);
    }
    
    /** Check profile update's that don't respect their validators. */
    @Test(dataProvider = DP_RS_USERS)
    public void shouldNotUpdateInvalidData(Rs rs) throws Exception {
        List<Object[]> dataAndErrList = Arrays.asList(
                new Object[]{ProfileFieldEnum.EMAIL, "test.foo.bar.com", "EMAIL_BAD_FORMAT"},
                new Object[]{ProfileFieldEnum.EMAIL, 1, "EMAIL_BAD_OBJECTCLASS"},
                new Object[]{ProfileFieldEnum.EMAIL, verylongString("averylongemail") + "@test.com", "EMAIL_TOO_LONG"},
                new Object[]{ProfileFieldEnum.CLANTAG, "aclantag!!!", "CLANTAG_BAD_FORMAT"},
                new Object[]{ProfileFieldEnum.CLANTAG, 1, "CLANTAG_BAD_OBJECTCLASS"},
                new Object[]{ProfileFieldEnum.CLANTAG, verylongString("averylongclantag"), "CLANTAG_TOO_LONG"},
                new Object[]{ProfileFieldEnum.NICKNAME, "anickname!!!", "NICKNAME_BAD_FORMAT"},
                new Object[]{ProfileFieldEnum.NICKNAME, 1, "NICKNAME_BAD_OBJECTCLASS"},
                new Object[]{ProfileFieldEnum.NICKNAME, verylongString("averylongnickname"), "NICKNAME_TOO_LONG"}
        );
        for (Object[] dataAndErr : dataAndErrList) {
            rs.getRequestSpecification()
                    .body(new ProfileUpdateForm((ProfileFieldEnum) dataAndErr[0], dataAndErr[1]))
                    .contentType(ContentType.JSON)
                    .put(getApiV1() + TEST_API_PROFILE + "/field")
                    .then()
                    .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                    .body(FIELD_ERRORS, Matchers.equalTo("ProfileUpdateFormException"),
                            FIELD_MESSAGE, Matchers.hasSize(1),
                            FIELD_MESSAGE, Matchers.contains(dataAndErr[2].toString()));
        }
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldUpdatePassword(Rs rs) throws Exception {
        rs.getRequestSpecification()
                .body(new UserPasswordUpdateForm(rs.getPassword(), "a new password", "a new password"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/password")
                .then()
                .statusCode(SC_OK);
        RestAssured.given().authentication().basic(rs.getUsername(), "a new password")
                .get(getApiV1() + TEST_API_PROFILE)
                .then().statusCode(SC_OK);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCheckUpdatedPassword(Rs rs) throws Exception {
        rs.getRequestSpecification()
                .body(new UserPasswordUpdateForm(rs.getPassword(), "a new password", "a new password"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/password")
                .then()
                .statusCode(SC_OK);
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE)
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldNotUpdatePassword_anonymous() {
        whenAnonymous().getRequestSpecification()
                .body(new UserPasswordUpdateForm(pwd(0), "a new password", "a new password"))
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_PROFILE + "/password")
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldNotUpdatePasswordInvalidData(Rs rs) {
        String p1Pwd = pwd(0);
        List<String[]> passowrds = Arrays.asList(
                new String[]{p1Pwd, "a new password", "a different password", "NEW_PASSWORD_NOT_CHECKED"},
                new String[]{"", "a new password", "a new password", "OLD_PASSWORD_EMPTY"},
                new String[]{null, "a new password", "a new password", "OLD_PASSWORD_EMPTY"},
                new String[]{pwd(0), "", "a different password", "NEW_PASSWORD_EMPTY"},
                new String[]{p1Pwd, "a new password", "", "CHECK_PASSWORD_EMPTY"},
                new String[]{p1Pwd, "", "", "NEW_PASSWORD_EMPTY"},
                new String[]{p1Pwd, "a new password", "", "CHECK_PASSWORD_EMPTY"},
                new String[]{p1Pwd, p1Pwd, p1Pwd, "OLD_PASSWORD_EQUALS_NEW"},
                new String[]{p1Pwd, p1Pwd, "a new password", "OLD_PASSWORD_EQUALS_NEW"},
                new String[]{p1Pwd, "a new password", p1Pwd, "NEW_PASSWORD_NOT_CHECKED"},
                new String[]{p1Pwd, null, null, "NEW_PASSWORD_EMPTY"}
        );
        for (String[] pwd : passowrds) {
            rs.getRequestSpecification()
                    .body(new UserPasswordUpdateForm(pwd[0], pwd[1], pwd[2]))
                    .contentType(ContentType.JSON)
                    .put(getApiV1() + TEST_API_PROFILE + "/password")
                    .then()
                    .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                    .body(FIELD_ERRORS, Matchers.equalTo("UserPasswordUpdateFormException"),
                            FIELD_MESSAGE, Matchers.hasSize(1),
                            FIELD_MESSAGE, Matchers.contains(pwd[3]));
        }
        // invalid operations should not be effective
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_PROFILE)
                .then().statusCode(SC_OK);
    }
    
    /** Only {@link Profile.Validation#MAX_EVENTS} most recent profile's friendshipEvents should be kept. */
    @Test
    public void shouldCheckEventsMaintenance() throws Exception {
        String p1ProfileId = profileId(1);
        String p2ProfileId = profileId(2);
        assertEquals(0, profileService.readOne(p1ProfileId).getFriendshipEvents().size());
        for (int i = 0; i < (Profile.Validation.MAX_EVENTS / 2) + 5; i++) {
            whenP1().getRequestSpecification()
                    .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + p2ProfileId)
                    .then()
                    .statusCode(SC_OK);
            whenP1().getRequestSpecification()
                    .post(getApiV1() + TEST_API_PROFILE + "/cancelfriendship/profile/" + p2ProfileId)
                    .then()
                    .statusCode(SC_OK);
            
        }
        assertEquals(profileService.readOne(p1ProfileId).getFriendshipEvents().size(), Profile.Validation.MAX_EVENTS);
    }
    
    /**
     * Utility method to compute a long string.
     * @param base string to repeat.
     * @return repeated string.
     */
    private String verylongString(String base) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append(base);
        }
        return sb.toString();
    }
}
