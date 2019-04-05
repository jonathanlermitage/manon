package manon.service.user.impl;

import manon.document.user.UserStats;
import manon.util.Tools;
import manon.util.basetest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class UserStatsServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Test
    public void shouldFindAll() {
        userStatsRepository.saveAll(Arrays.asList(
            UserStats.builder().build(),
            UserStats.builder().build()
        ));
        
        Assertions.assertThat(userStatsService.findAll()).hasSize(2);
    }
    
    @Test
    public void shouldSave() {
        LocalDateTime before = Tools.now();
        userStatsService.save(UserStats.builder().nbUsers(100).build());
        LocalDateTime after = Tools.now();
        
        List<UserStats> us = userStatsRepository.findAll();
        Assertions.assertThat(us).hasSize(1);
        Assertions.assertThat(us.get(0).getCreationDate()).isBetween(before, after);
        Assertions.assertThat(us.get(0).getNbUsers()).isEqualTo(100);
    }
}
