package manon.game.spacestory.world.repository;

import manon.game.spacestory.world.document.World;
import manon.game.spacestory.world.document.WorldSummary;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorldRepository extends MongoRepository<World, String> {
    
    boolean existsByName(String name);
    
    @Query(value = "{'id':?0 }")
    Optional<WorldSummary> findSummaryById(@NotNull String id);
    
    @Query(value = "{}")
    List<WorldSummary> findAllWorldSummaries();
}
