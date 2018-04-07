package manon.game.world.repository;

import manon.game.world.document.WorldSector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldSectorRepository extends MongoRepository<WorldSector, String> {
}
