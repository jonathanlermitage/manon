package manon.user.repository;

import manon.user.document.UsersStats;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends ReactiveMongoRepository<UsersStats, String> {
}
