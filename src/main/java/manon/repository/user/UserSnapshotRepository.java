package manon.repository.user;

import manon.document.user.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {

    long countAllByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    void deleteAllByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    void deleteAllByCreationDateBeforeOrCreationDateAfter(LocalDateTime start, LocalDateTime end);
}
