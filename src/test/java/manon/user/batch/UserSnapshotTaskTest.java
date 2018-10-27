package manon.user.batch;

import manon.app.batch.service.TaskRunnerService;
import manon.user.document.UserSnapshot;
import manon.user.document.UserStats;
import manon.user.service.UserService;
import manon.user.service.UserSnapshotService;
import manon.user.service.UserStatsService;
import manon.util.basetest.InitBeforeClass;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.DataProvider;
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
    
    @Value("${manon.batch.user-snapshot.chunk}")
    private int chunk;
    @Value("${manon.batch.user-snapshot.snapshot.max-age}")
    private int maxAge;
    
    @Override
    public int getNumberOfUsers() {
        return chunk * NB_BATCH_CHUNKS_TO_ENSURE_RELIABILITY;
    }
    
    @DataProvider
    public static Object[][] dataProviderShouldCompleteMultipleTimes() {
        return new Object[][]{
            {2, 3},
            {4, 6}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldCompleteMultipleTimes")
    public void shouldCompleteMultipleTimes(int snapshotsKept, int nbStats) throws Exception {
        assertEquals(chunk, 10);
        assertEquals(maxAge, 30);
        
        //GIVEN existing old, present, recent and future User snapshots
        List<Integer> delays = Arrays.asList(-1 - maxAge, 0 - maxAge, 1 - maxAge, 0, 1);
        List<UserSnapshot> userSnapshots = new ArrayList<>();
        
        // workaround: can't save custom creationDate at creation, do it at update
        for (int i = 0; i < delays.size(); i++) {
            userSnapshots.add(UserSnapshot.builder().user(user(1)).build());
        }
        userSnapshotService.save(userSnapshots);
        for (int i = 0; i < delays.size(); i++) {
            userSnapshots.set(i, userSnapshots.get(i).toBuilder().creationDate(nowPlusDays(delays.get(i))).build());
        }
        userSnapshotService.save(userSnapshots);
        
        long expectedTodayUserSnapshots = userService.count();
        long expectedUserSnapshots = expectedTodayUserSnapshots + snapshotsKept; // keep 1 - maxAge and 0, other are too old, present or future
        for (int i = 0; i < NB_TESTS_TO_ENSURE_BATCH_REPEATABILITY; i++) {
            
            //WHEN run User snapshot job
            ExitStatus run = taskRunnerService.run(UserSnapshotTask.JOB_NAME);
            
            //THEN should keep recent User snapshots only and generate today's ones
            assertEquals(run, COMPLETED);
            assertEquals(userSnapshotService.countToday(), expectedTodayUserSnapshots);
            assertEquals(userSnapshotService.count(), expectedUserSnapshots);
            Stream.of(1, 2).forEach(idx -> assertThat(userSnapshotService.findOne(userSnapshots.get(idx).getId())).isPresent());
            Stream.of(0, 3, 4).forEach(idx -> assertThat(userSnapshotService.findOne(userSnapshots.get(idx).getId())).isNotPresent());
        }
        
        //THEN statistics are written
        List<UserStats> userStats = userStatsService.findAll();
        assertThat(userStats).isNotNull().hasSize(nbStats);
        userStats.forEach(ps -> assertEquals(ps.getNbUsers(), expectedTodayUserSnapshots));
    }
    
    
    /** Get current date plus given days. */
    private Date nowPlusDays(int nbDays) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, nbDays);
        return cal.getTime();
    }
}
