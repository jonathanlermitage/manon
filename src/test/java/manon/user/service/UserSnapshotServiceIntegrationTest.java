package manon.user.service;

import lombok.SneakyThrows;
import manon.user.document.User;
import manon.user.document.UserSnapshot;
import manon.util.Tools;
import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSnapshotServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private UserSnapshotService userSnapshotService;
    
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
    
    public Object[] dataProviderCount() {
        return new Object[][]{
            {0, 0},
            {1, 1},
            {2, 2}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderCount")
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
        LocalDateTime yesterday = Tools.yesterday();
        LocalDateTime tomorrow = Tools.tomorrow();
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
            assertThat(userSnapshot.getCreationDate())
                .isBetween(yesterday, tomorrow));
    }
    
    @Test
    public void shouldKeepRecent() {
        LocalDateTime before2Days = Tools.nowMinusDays(2);
        for (int i = 0; i < 3; i++) {
            userSnapshotRepository.saveAll(Arrays.asList(
                makeUserSnapshot(),
                saveUserSnapshot().toBuilder().creationDate(Tools.nowMinusDays(3)).build(),
                saveUserSnapshot().toBuilder().creationDate(before2Days).build(),
                saveUserSnapshot().toBuilder().creationDate(Tools.yesterday()).build(),
                saveUserSnapshot().toBuilder().creationDate(Tools.tomorrow()).build()
            ));
        }
        
        userSnapshotService.keepRecent(2);
        
        assertThat(userSnapshotService.count()).isEqualTo(9);
        userSnapshotRepository.findAll().forEach(userSnapshot ->
            assertThat(userSnapshot.getCreationDate()).isAfterOrEqualTo(before2Days));
    }
    
    @Test
    public void shouldSave() {
        LocalDateTime before = Tools.now();
        userSnapshotService.save(Arrays.asList(
            makeUserSnapshot(),
            makeUserSnapshot()
        ));
        LocalDateTime after = Tools.now();
        
        userSnapshotRepository.findAll().forEach(userSnapshot -> {
            assertThat(userSnapshot.getUserId()).isEqualTo(user(1).getId());
            assertThat(userSnapshot.getCreationDate()).isBetween(before, after);
        });
    }
    
    @SneakyThrows
    private UserSnapshot makeUserSnapshot() {
        User user = userService.findByUsername(name(1)).orElseThrow(Exception::new);
        return UserSnapshot.from(user);
    }
    
    private UserSnapshot saveUserSnapshot() {
        return userSnapshotRepository.save(makeUserSnapshot());
    }
}
