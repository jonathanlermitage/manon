package manon.app.management.repository;

import manon.app.management.document.AppTrace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTraceRepository extends MongoRepository<AppTrace, String> {
}
