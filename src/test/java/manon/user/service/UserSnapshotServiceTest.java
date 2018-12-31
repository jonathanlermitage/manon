package manon.user.service;

import lombok.SneakyThrows;
import manon.user.document.User;
import manon.user.document.UserSnapshot;
import manon.user.repository.UserSnapshotRepository;
import manon.util.Tools;
import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSnapshotServiceTest extends AbstractInitBeforeClass {
    
    @Autowired
    private UserSnapshotRepository userSnapshotRepository;
    
    @Autowired
    private UserSnapshotService userSnapshotService;
    
    @Override
    public void additionalBeforeMethod() {
        userSnapshotRepository.deleteAll();
    }
    
    @Test
    public void shouldFindOne() throws Exception {
        saveUserSnapshot();
        UserSnapshot us = saveUserSnapshot();
        saveUserSnapshot();
        
        assertThat(userSnapshotService.findOne(us.getId()).orElseThrow(Exception::new)).isEqualTo(us);
    }
    
    @Test
    public void shouldFailFindOne() {
        assertThat(userSnapshotService.findOne(UNKNOWN_ID)).isEmpty();
    }
    
    @DataProvider
    public static Object[] dataProviderCount() {
        return new Object[][]{
            {0, 0},
            {1, 1},
            {2, 2}
        };
    }
    
    @Test(dataProvider = "dataProviderCount")
    public void shouldCount(int created, int expected) {
        for (int i = 0; i < created; i++) {
            saveUserSnapshot();
        }
        
        assertThat(userSnapshotService.count()).isEqualTo(expected);
    }
    
    @Test
    public void shouldCountToday() {
        for (int i = 0; i < 3; i++) {
            userSnapshotRepository.saveAll(Arrays.asList(
                makeUserSnapshot(),
                saveUserSnapshot().toBuilder().creationDate(Tools.yesterday()).build(),
                saveUserSnapshot().toBuilder().creationDate(Tools.tomorrow()).build()
            
            ));
        }
        
        assertThat(userSnapshotService.countToday()).isEqualTo(3);
    }
    
    @Test
    public void shouldDeleteToday() {
        Date yesterday = Tools.yesterday();
        Date tomorrow = Tools.tomorrow();
        for (int i = 0; i < 3; i++) {
            userSnapshotRepository.saveAll(Arrays.asList(
                makeUserSnapshot(),
                saveUserSnapshot().toBuilder().creationDate(yesterday).build(),
                saveUserSnapshot().toBuilder().creationDate(tomorrow).build()
            ));
        }
        
        userSnapshotService.deleteToday();
        
        assertThat(userSnapshotService.countToday()).isEqualTo(0);
        assertThat(userSnapshotService.count()).isEqualTo(6);
        userSnapshotRepository.findAll().forEach(userSnapshot ->
            assertThat(userSnapshot.getCreationDate()).isIn(yesterday, tomorrow, true, true));
    }
    
    @Test
    public void shouldKeepRecent() {
        Date before2 = Tools.daysBefore(2);
        for (int i = 0; i < 3; i++) {
            userSnapshotRepository.saveAll(Arrays.asList(
                makeUserSnapshot(),
                saveUserSnapshot().toBuilder().creationDate(Tools.daysBefore(3)).build(),
                saveUserSnapshot().toBuilder().creationDate(before2).build(),
                saveUserSnapshot().toBuilder().creationDate(Tools.yesterday()).build(),
                saveUserSnapshot().toBuilder().creationDate(Tools.tomorrow()).build()
            ));
        }
        
        userSnapshotService.keepRecent(2);
        
        assertThat(userSnapshotService.count()).isEqualTo(9);
        userSnapshotRepository.findAll().forEach(userSnapshot ->
            assertThat(userSnapshot.getCreationDate()).isAfterOrEqualsTo(before2));
    }
    
    @Test
    public void shouldSave() {
        Date before = new Date();
        userSnapshotService.save(Arrays.asList(
            makeUserSnapshot(),
            makeUserSnapshot()
        ));
        Date after = new Date();
        
        userSnapshotRepository.findAll().forEach(userSnapshot -> {
            assertThat(userSnapshot.getUser()).isEqualTo(user(1));
            assertThat(userSnapshot.getCreationDate()).isBetween(before, after, true, true);
        });
    }
    
    @SneakyThrows
    private UserSnapshot makeUserSnapshot() {
        User user = userService.findByUsername(name(1)).orElseThrow(Exception::new);
        return UserSnapshot.builder().user(user).build();
    }
    
    private UserSnapshot saveUserSnapshot() {
        return userSnapshotRepository.save(makeUserSnapshot());
    }
}
