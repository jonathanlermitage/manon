package manon.user.service;

import manon.user.document.UsersStats;

import java.util.List;

public interface UserStatsService {
    
    List<UsersStats> findAll();
    
    void save(UsersStats entity);
}
