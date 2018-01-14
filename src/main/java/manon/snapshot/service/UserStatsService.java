package manon.snapshot.service;

import manon.snapshot.document.UsersStats;

import java.util.List;

public interface UserStatsService {
    
    List<UsersStats> findAll();
    
    void save(UsersStats entity);
}
