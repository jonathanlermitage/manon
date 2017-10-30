package manon.snapshot.service;

import lombok.RequiredArgsConstructor;
import manon.snapshot.document.ProfilesStats;
import manon.snapshot.repository.ProfilesStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ProfilesStatsService")
@RequiredArgsConstructor
public class ProfilesStatsServiceImpl implements ProfilesStatsService {
    
    private final ProfilesStatsRepository profilesStatsRepository;
    
    public List<ProfilesStats> findAll() {
        return profilesStatsRepository.findAll();
    }
    
    @Override
    public void save(ProfilesStats entity) {
        profilesStatsRepository.save(entity);
    }
}
