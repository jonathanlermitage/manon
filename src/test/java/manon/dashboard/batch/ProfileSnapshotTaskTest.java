package manon.dashboard.batch;

import manon.batch.service.TaskRunnerService;
import manon.dashboard.document.ProfileSnapshot;
import manon.dashboard.service.ProfileSnapshotService;
import manon.profile.service.ProfileService;
import manon.util.basetest.InitBeforeClass;
import org.jooq.lambda.Seq;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static manon.util.Tools.nowPlusDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.testng.Assert.assertEquals;

public class ProfileSnapshotTaskTest extends InitBeforeClass {
    
    @Autowired
    private TaskRunnerService taskRunnerService;
    
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProfileSnapshotService profileSnapshotService;
    
    @Override
    public int getNumberOfProfiles() {
        return 150;
    }
    
    @Test
    public void shouldComplete() throws Exception {
        //GIVEN existing old, present, recent and future Profile snapshots
        List<Integer> delays = Arrays.asList(-35, -25, -1, 0, 1, 25, 35);
        List<ProfileSnapshot> profileSnapshots = new ArrayList<>();
        for (int i = 0; i < delays.size(); i++) {
            profileSnapshots.add(ProfileSnapshot.builder().build());
        }
        profileSnapshotService.save(profileSnapshots);
        for (int i = 0; i < delays.size(); i++) {
            profileSnapshots.set(i, profileSnapshots.get(i).toBuilder().creationDate(nowPlusDays(delays.get(i))).build());
        }
        profileSnapshotService.save(profileSnapshots);
        long expectedTodayProfileSnapshots = profileService.count();
        long expectedProfileSnapshots = expectedTodayProfileSnapshots + 2; // keep -25 and -1, other are old, present or future
        for (int i = 0; i < 3; i++) {
            
            //WHEN run Profile snapshot job
            ExitStatus run = taskRunnerService.run(ProfileSnapshotTask.JOB_NAME);
            
            //THEN should keep recent Profile snapshots only and generate today's ones
            assertEquals(COMPLETED, run);
            assertEquals(profileSnapshotService.countToday(), expectedTodayProfileSnapshots);
            assertEquals(profileSnapshotService.count(), expectedProfileSnapshots);
            Seq.of(1, 2).forEach(integer -> assertThat(profileSnapshotService.findOne(profileSnapshots.get(integer).getId())).isPresent());
            Seq.of(0, 3, 4, 5, 6).forEach(integer -> assertThat(profileSnapshotService.findOne(profileSnapshots.get(integer).getId())).isNotPresent());
        }
    }
}
