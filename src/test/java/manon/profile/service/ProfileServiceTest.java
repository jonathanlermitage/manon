package manon.profile.service;

import manon.util.basetest.InitBeforeClass;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static manon.profile.document.Profile.Default.INITIAL_SKILL;
import static org.testng.Assert.assertEquals;

public class ProfileServiceTest extends InitBeforeClass {
    
    @Override
    public int getNumberOfProfiles() {
        return 3;
    }
    
    @Autowired
    protected ProfileService profileService;
    
    /** Get profile's skill. */
    @Test
    public void shouldCheckProfileSkill() {
        String unknownProfileId = new ObjectId().toString();
        assertEquals(profileService.getSkill(profileId(1)), INITIAL_SKILL);
        assertEquals(profileService.getSkill(profileId(2)), INITIAL_SKILL);
        assertEquals(profileService.getSkill(profileId(3)), INITIAL_SKILL);
        assertEquals(profileService.getSkill(unknownProfileId), 0L);
    }
    
    /** Get team's skill. */
    @Test
    public void shouldCheckTeamSkill() {
        String unknownProfileId = new ObjectId().toString();
        assertEquals(profileService.sumSkill(Arrays.asList(profileId(1), profileId(2), profileId(3))), 3 * INITIAL_SKILL);
        assertEquals(profileService.sumSkill(Arrays.asList(profileId(1), profileId(2))), 2 * INITIAL_SKILL);
        assertEquals(profileService.sumSkill(Arrays.asList(profileId(1), profileId(1))), INITIAL_SKILL);
        assertEquals(profileService.sumSkill(Arrays.asList(profileId(1), unknownProfileId, profileId(3))), 2 * INITIAL_SKILL);
        assertEquals(profileService.sumSkill(Arrays.asList(unknownProfileId, unknownProfileId)), 0L);
        assertEquals(profileService.sumSkill(Collections.singletonList(unknownProfileId)), 0L);
    }
}
