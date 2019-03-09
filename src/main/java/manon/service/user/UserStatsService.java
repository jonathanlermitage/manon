package manon.service.user;

import manon.document.user.UserStats;

import java.util.List;

public interface UserStatsService {
    
    List<UserStats> findAll();
    
    void save(UserStats entity);
}
