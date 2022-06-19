package manon.service.user;

import manon.document.user.UserStatsEntity;
import manon.util.ExistForTesting;

import java.util.List;

public interface UserStatsService {

    void persist(UserStatsEntity entity);

    @ExistForTesting(why = {"UserSnapshotJobConfigIntegrationTest", "UserStatsServiceIntegrationTest"})
    List<UserStatsEntity> findAll();

    @ExistForTesting(why = "UserStatsServiceIntegrationTest")
    void persistAll(Iterable<UserStatsEntity> entities);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
