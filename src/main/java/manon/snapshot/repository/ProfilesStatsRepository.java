package manon.snapshot.repository;

import manon.snapshot.document.ProfilesStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilesStatsRepository extends MongoRepository<ProfilesStats, String> {
}
