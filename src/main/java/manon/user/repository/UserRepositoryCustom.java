package manon.user.repository;

import manon.user.UserNotFoundException;
import manon.user.registration.RegistrationStateEnum;

public interface UserRepositoryCustom {
    
    void setPassword(String id, String password) throws UserNotFoundException;
    
    void setRegistrationState(String id, RegistrationStateEnum registrationState) throws UserNotFoundException;
}
