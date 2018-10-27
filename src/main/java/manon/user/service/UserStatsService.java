package manon.user.service;

import manon.user.document.UserStats;

import java.util.List;

public interface UserStatsService {
    
    List<UserStats> findAll();
    
    void save(UserStats entity);
}
