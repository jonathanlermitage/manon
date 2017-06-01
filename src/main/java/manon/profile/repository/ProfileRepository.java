package manon.profile.repository;

import manon.profile.document.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String>, ProfileRepositoryCustom {
    
    Optional<Profile> findById(String id);
}
