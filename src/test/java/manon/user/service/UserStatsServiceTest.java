package manon.user.service;

import manon.user.document.UsersStats;
import manon.user.repository.UserStatsRepository;
import manon.util.basetest.InitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class UserStatsServiceTest extends InitBeforeClass {
    
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
            UsersStats.builder().build(),
            UsersStats.builder().build()
        ));
        
        assertThat(userStatsService.findAll()).hasSize(2);
    }
    
    @Test
    public void shouldSave() {
        Date before = new Date();
        userStatsService.save(UsersStats.builder().nbUsers(100).build());
        Date after = new Date();
        
        List<UsersStats> us = userStatsRepository.findAll();
        assertEquals(us.size(), 1);
        assertThat(us.get(0).getCreationDate()).isBetween(before, after, true, true);
        assertEquals(us.get(0).getNbUsers(), 100);
    }
}
