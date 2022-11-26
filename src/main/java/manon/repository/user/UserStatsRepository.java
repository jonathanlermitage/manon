package manon.repository.user;

import manon.document.user.UserStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {
}
