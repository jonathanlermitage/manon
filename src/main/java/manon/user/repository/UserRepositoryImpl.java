package manon.user.repository;

import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public void setPassword(String id, String password) throws UserNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set("password", password),
                User.class).getN()) {
            throw new UserNotFoundException(id);
        }
    }
    
    @Override
    public void setRegistrationState(String id, RegistrationStateEnum registrationState) throws UserNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set("registrationState", registrationState),
                User.class).getN()) {
            throw new UserNotFoundException(id);
        }
    }
}
