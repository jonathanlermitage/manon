package manon.game.world.repository;

import manon.game.world.document.WorldPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldPointRepository extends MongoRepository<WorldPoint, String> {
}
