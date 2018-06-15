package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.UsersStats;
import manon.user.repository.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    
    private final UserStatsRepository userStatsRepository;
    
    public List<UsersStats> findAll() {
        return userStatsRepository.findAll().collectList().block();
    }
    
    @Override
    public void save(UsersStats entity) {
        userStatsRepository.save(entity).block();
    }
}
