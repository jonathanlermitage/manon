package manon.user.service;

import manon.user.document.UserStats;
import manon.user.repository.UserStatsRepository;
import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatsServiceIntegrationTest extends AbstractInitBeforeClass {
    
    @Autowired
    private UserStatsRepository userStatsRepository;
    
    @Autowired
    private UserStatsService userStatsService;
    
    @BeforeMethod
    @Override
    public void additionalBeforeMethod() {
        userStatsRepository.deleteAll();
    }
    
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
        Date before = new Date();
        userStatsService.save(UserStats.builder().nbUsers(100).build());
        Date after = new Date();
        
        List<UserStats> us = userStatsRepository.findAll();
        assertThat(us).hasSize(1);
        assertThat(us.get(0).getCreationDate()).isBetween(before, after, true, true);
        assertThat(us.get(0).getNbUsers()).isEqualTo(100);
    }
}
