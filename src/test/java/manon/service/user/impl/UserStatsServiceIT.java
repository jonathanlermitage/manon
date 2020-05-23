package manon.service.user.impl;

import manon.document.user.UserStatsEntity;
import manon.util.Tools;
import manon.util.basetest.AbstractIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class UserStatsServiceIT extends AbstractIT {

    @Test
    void shouldFindAll() {
        userStatsService.saveAll(Arrays.asList(
            UserStatsEntity.builder().build(),
            UserStatsEntity.builder().build()
        ));

        Assertions.assertThat(userStatsService.findAll()).hasSize(2);
    }

    @Test
    void shouldSave() {
        LocalDateTime before = Tools.now();
        userStatsService.save(UserStatsEntity.builder().nbUsers(100).build());
        LocalDateTime after = Tools.now();

        List<UserStatsEntity> us = userStatsService.findAll();
        Assertions.assertThat(us).hasSize(1);
        Assertions.assertThat(us.get(0).getCreationDate()).isBetween(before, after);
        Assertions.assertThat(us.get(0).getNbUsers()).isEqualTo(100);
    }
}
