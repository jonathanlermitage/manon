package manon.snapshot.service;

import manon.snapshot.document.ProfilesStats;

import java.util.List;

public interface ProfilesStatsService {
    
    List<ProfilesStats> findAll();
    
    void save(ProfilesStats entity);
}
