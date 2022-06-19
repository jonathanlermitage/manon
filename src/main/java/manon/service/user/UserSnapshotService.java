package manon.service.user;

import manon.document.user.UserSnapshotEntity;
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
    Optional<UserSnapshotEntity> findOne(long id);

    @ExistForTesting
    List<UserSnapshotEntity> findAll();

    @ExistForTesting
    UserSnapshotEntity persist(UserSnapshotEntity entity);

    @ExistForTesting
    void persistAll(Iterable<UserSnapshotEntity> entities);

    @ExistForTesting
    void updateAll(Iterable<UserSnapshotEntity> entities);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
