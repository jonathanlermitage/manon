package manon.user.service;

import manon.user.document.UserStats;
import manon.util.Tools;
import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatsServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private UserStatsService userStatsService;
    
    @Test
    public void shouldFindAll() {
        userStatsRepository.saveAll(Arrays.asList(
            UserStats.builder().build(),
            UserStats.builder().build()
        ));
        
        assertThat(userStatsService.findAll()).hasSize(2);
    }
    
    @Test
    public void shouldSave() {
        LocalDateTime before = Tools.now();
        userStatsService.save(UserStats.builder().nbUsers(100).build());
        LocalDateTime after = Tools.now();
        
        List<UserStats> us = userStatsRepository.findAll();
        assertThat(us).hasSize(1);
        assertThat(us.get(0).getCreationDate()).isBetween(before, after);
        assertThat(us.get(0).getNbUsers()).isEqualTo(100);
    }
}
