package manon.snapshot.batch;

import manon.batch.service.TaskRunnerService;
import manon.snapshot.document.UserSnapshot;
import manon.snapshot.document.UsersStats;
import manon.snapshot.service.UserSnapshotService;
import manon.snapshot.service.UserStatsService;
import manon.user.service.UserService;
import manon.util.basetest.InitBeforeClass;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.testng.Assert.assertEquals;

public class UserSnapshotTaskTest extends InitBeforeClass {
    
    @Autowired
    private TaskRunnerService taskRunnerService;
    
    @Autowired
    private UserService userService;
    @Autowired
    private UserSnapshotService userSnapshotService;
    @Autowired
    private UserStatsService userStatsService;
    
    @Override
    public int getNumberOfUsers() {
        return 100;
    }
    
    @Test
    public void shouldComplete() throws Exception {
        //GIVEN existing old, present, recent and future User snapshots
        List<Integer> delays = Arrays.asList(-35, -25, -1, 0, 1, 25, 35);
        List<UserSnapshot> userSnapshots = new ArrayList<>();
        for (int i = 0; i < delays.size(); i++) {
            userSnapshots.add(UserSnapshot.builder().build());
        }
        userSnapshotService.save(userSnapshots);
        for (int i = 0; i < delays.size(); i++) {
            userSnapshots.set(i, userSnapshots.get(i).toBuilder().creationDate(nowPlusDays(delays.get(i))).build());
        }
        userSnapshotService.save(userSnapshots);
        long expectedTodayUserSnapshots = userService.count();
        long expectedUserSnapshots = expectedTodayUserSnapshots + 2; // keep -25 and -1, other are old, present or future
        for (int i = 0; i < 3; i++) {
            
            //WHEN run User snapshot job
            ExitStatus run = taskRunnerService.run(UserSnapshotTask.JOB_NAME);
            
            //THEN should keep recent User snapshots only and generate today's ones
            assertEquals(run, COMPLETED);
            assertEquals(userSnapshotService.countToday(), expectedTodayUserSnapshots);
            assertEquals(userSnapshotService.count(), expectedUserSnapshots);
            Stream.of(1, 2).forEach(idx -> assertThat(userSnapshotService.findOne(userSnapshots.get(idx).getId())).isPresent());
            Stream.of(0, 3, 4, 5, 6).forEach(idx -> assertThat(userSnapshotService.findOne(userSnapshots.get(idx).getId())).isNotPresent());
        }
        
        //THEN statistics are written
        List<UsersStats> usersStats = userStatsService.findAll();
        assertThat(usersStats).isNotNull().hasSize(3);
        usersStats.forEach(ps -> assertEquals(ps.getNbUsers(), expectedTodayUserSnapshots));
    }
    
    
    /** Get current date plus given days. */
    private Date nowPlusDays(int nbDays) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, nbDays);
        return cal.getTime();
    }
}
