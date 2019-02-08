package manon.user.repository;

import manon.user.document.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {
    
    long countAllByCreationDateBetween(Date start, Date end);
    
    void deleteAllByCreationDateBetween(Date start, Date end);
    
    void deleteAllByCreationDateBeforeOrCreationDateAfter(Date start, Date end);
}
