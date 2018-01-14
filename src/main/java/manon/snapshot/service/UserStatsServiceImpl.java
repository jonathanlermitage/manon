package manon.snapshot.service;

import lombok.RequiredArgsConstructor;
import manon.snapshot.document.UsersStats;
import manon.snapshot.repository.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    
    private final UserStatsRepository userStatsRepository;
    
    public List<UsersStats> findAll() {
        return userStatsRepository.findAll();
    }
    
    @Override
    public void save(UsersStats entity) {
        userStatsRepository.save(entity);
    }
}
