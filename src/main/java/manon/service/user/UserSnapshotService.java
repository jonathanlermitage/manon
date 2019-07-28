package manon.service.user;

import manon.document.user.UserSnapshot;
import manon.util.ExistForTesting;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface UserSnapshotService {
    
    /** Count User snapshots created today. */
    long countToday();
    
    /** Delete User snapshots created today. */
    void deleteToday();
    
    /** Keep recent User snapshots only.
     * Snapshots older than {@code maxAge} days are deleted, in addition to future ones. */
    void keepRecent(Duration maxAge);
    
    @ExistForTesting
    long count();
    
    @ExistForTesting
    Optional<UserSnapshot> findOne(long id);
    
    @ExistForTesting
    List<UserSnapshot> findAll();
    
    @ExistForTesting
    UserSnapshot save(UserSnapshot entity);
    
    @ExistForTesting
    void saveAll(Iterable<UserSnapshot> entities);
    
    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
