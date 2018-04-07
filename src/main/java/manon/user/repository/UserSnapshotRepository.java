package manon.user.repository;

import manon.user.document.UserSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserSnapshotRepository extends MongoRepository<UserSnapshot, String> {
    
    @Query(value = "{'creationDate':{$gte:?0, $lte:?1}}", count = true)
    long countAllByCreationDateInRange(Date start, Date end);
    
    @Query(value = "{'creationDate':{$gte:?0, $lte:?1}}", delete = true)
    void deleteAllByCreationDateInRange(Date start, Date end);
    
    @Query(value = "{$or:[ {'creationDate':{$lt:?0}}, {'creationDate':{$gt:?1}} ]}", delete = true)
    void deleteAllByCreationDateOutsideRange(Date start, Date end);
}
