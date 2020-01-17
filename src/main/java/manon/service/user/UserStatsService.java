package manon.service.user;

import manon.document.user.UserStats;
import manon.util.ExistForTesting;

import java.util.List;

public interface UserStatsService {

    void save(UserStats entity);

    @ExistForTesting(why = {"UserSnapshotJobConfigIntegrationTest", "UserStatsServiceIntegrationTest"})
    List<UserStats> findAll();

    @ExistForTesting(why = "UserStatsServiceIntegrationTest")
    void saveAll(Iterable<UserStats> entities);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
