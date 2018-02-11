package manon.user.snapshot.service;

import manon.user.snapshot.document.UsersStats;

import java.util.List;

public interface UserStatsService {
    
    List<UsersStats> findAll();
    
    void save(UsersStats entity);
}
