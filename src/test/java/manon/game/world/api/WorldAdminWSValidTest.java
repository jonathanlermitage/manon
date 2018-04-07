package manon.game.world.api;

import io.restassured.response.ValidatableResponse;
import manon.game.world.form.WorldRegistrationForm;
import manon.util.basetest.MockBeforeClass;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static manon.game.world.document.World.Validation.NAME_MAX_LENGTH;
import static manon.game.world.document.World.Validation.NAME_MIN_LENGTH;
import static manon.game.world.document.World.Validation.NAME_SIZE_ERRMSG;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.SECTOR_HEIGHT_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.SECTOR_MAX_HEIGHT;
import static manon.game.world.document.World.Validation.SECTOR_MAX_WIDTH;
import static manon.game.world.document.World.Validation.SECTOR_MIN_HEIGHT;
import static manon.game.world.document.World.Validation.SECTOR_MIN_WIDTH;
import static manon.game.world.document.World.Validation.SECTOR_WIDTH_VAL_ERRMSG;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;

public class WorldAdminWSValidTest extends MockBeforeClass {
    
    @DataProvider
    public Object[][] dataProviderShouldValidateRegister() {
        WorldRegistrationForm validForm = WorldRegistrationForm.builder()
                .name("WORLD_1")
                .sectorWidth(SECTOR_MAX_WIDTH)
                .sectorHeight(SECTOR_MAX_HEIGHT)
                .nbSectorsHorizontal(NB_SECTORS_HORIZONTAL_MAX_VAL)
                .nbSectorsVertical(NB_SECTORS_VERTICAL_MAX_VAL)
                .nbPointsPerSector(NB_POINTS_PER_SECTOR_MAX_VAL)
                .build();
        return new Object[][]{
                {SC_CREATED, validForm, null},
                {SC_CREATED, validForm.toBuilder()
                        .name(repeat("N", NAME_MIN_LENGTH))
                        .sectorWidth(1)
                        .sectorHeight(1)
                        .nbSectorsHorizontal(1)
                        .nbSectorsVertical(1)
                        .nbPointsPerSector(1)
                        .build(),
                        null},
                {SC_BAD_REQUEST, validForm.toBuilder().name(verylongString("N")).build(), NAME_SIZE_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().sectorWidth(SECTOR_MAX_WIDTH + 1).build(), SECTOR_WIDTH_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().sectorHeight(SECTOR_MAX_HEIGHT + 1).build(), SECTOR_HEIGHT_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbSectorsHorizontal(NB_SECTORS_HORIZONTAL_MAX_VAL + 1).build(), NB_SECTORS_HORIZONTAL_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbSectorsVertical(NB_SECTORS_VERTICAL_MAX_VAL + 1).build(), NB_SECTORS_VERTICAL_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbPointsPerSector(NB_POINTS_PER_SECTOR_MAX_VAL + 1).build(), NB_POINTS_PER_SECTOR_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder()
                        .name(repeat("N", NAME_MAX_LENGTH + 1))
                        .sectorWidth(SECTOR_MAX_WIDTH + 1)
                        .sectorHeight(SECTOR_MAX_HEIGHT + 1)
                        .nbSectorsHorizontal(NB_SECTORS_HORIZONTAL_MAX_VAL + 1)
                        .nbSectorsVertical(NB_SECTORS_VERTICAL_MAX_VAL + 1)
                        .nbPointsPerSector(NB_POINTS_PER_SECTOR_MAX_VAL + 1)
                        .build(),
                        NAME_SIZE_ERRMSG, SECTOR_WIDTH_VAL_ERRMSG, SECTOR_HEIGHT_VAL_ERRMSG,
                        NB_SECTORS_HORIZONTAL_VAL_ERRMSG, NB_SECTORS_VERTICAL_VAL_ERRMSG, NB_POINTS_PER_SECTOR_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().name(null).build(), NAME_SIZE_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().name("").build(), NAME_SIZE_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().sectorWidth(SECTOR_MIN_WIDTH - 1).build(), SECTOR_WIDTH_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().sectorHeight(SECTOR_MIN_HEIGHT - 1).build(), SECTOR_HEIGHT_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbSectorsHorizontal(NB_SECTORS_HORIZONTAL_MIN_VAL - 1).build(), NB_SECTORS_HORIZONTAL_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbSectorsVertical(NB_SECTORS_VERTICAL_MIN_VAL - 1).build(), NB_SECTORS_VERTICAL_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder().nbPointsPerSector(NB_POINTS_PER_SECTOR_MIN_VAL - 1).build(), NB_POINTS_PER_SECTOR_VAL_ERRMSG},
                {SC_BAD_REQUEST, validForm.toBuilder()
                        .name(repeat("N", NAME_MIN_LENGTH - 1))
                        .sectorWidth(SECTOR_MIN_WIDTH - 1)
                        .sectorHeight(SECTOR_MIN_HEIGHT - 1)
                        .nbSectorsHorizontal(NB_SECTORS_HORIZONTAL_MIN_VAL - 1)
                        .nbSectorsVertical(NB_SECTORS_VERTICAL_MIN_VAL - 1)
                        .nbPointsPerSector(NB_POINTS_PER_SECTOR_MIN_VAL - 1)
                        .build(),
                        NAME_SIZE_ERRMSG, SECTOR_WIDTH_VAL_ERRMSG, SECTOR_HEIGHT_VAL_ERRMSG,
                        NB_SECTORS_HORIZONTAL_VAL_ERRMSG, NB_SECTORS_VERTICAL_VAL_ERRMSG, NB_POINTS_PER_SECTOR_VAL_ERRMSG}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldValidateRegister")
    public void shouldValidateRegister(int statusCode, WorldRegistrationForm form, String[] errMsg) {
        ValidatableResponse response = whenAdmin().getRequestSpecification()
                .body(form)
                .contentType(JSON)
                .post(API_WORLD_ADMIN)
                .then()
                .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(ERRORS_MSG, Matchers.hasSize(errMsg.length),
                    ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }
}
