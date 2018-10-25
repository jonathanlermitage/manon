package manon.user.repository;

import manon.user.document.UsersStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends MongoRepository<UsersStats, String> {
}
