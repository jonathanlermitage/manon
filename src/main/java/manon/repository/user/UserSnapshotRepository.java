package manon.repository.user;

import manon.document.user.UserSnapshotEntity;
import manon.repository.WorkaroundUntilHibernate6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserSnapshotRepository
    extends JpaRepository<UserSnapshotEntity, Long>,
    WorkaroundUntilHibernate6<UserSnapshotEntity> {

    long countAllByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    void deleteAllByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    void deleteAllByCreationDateBeforeOrCreationDateAfter(LocalDateTime start, LocalDateTime end);
}
