package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.UserStats;
import manon.user.repository.UserStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStatsServiceImpl implements UserStatsService {
    
    private final UserStatsRepository userStatsRepository;
    
    public List<UserStats> findAll() {
        return userStatsRepository.findAll();
    }
    
    @Override
    public void save(UserStats entity) {
        userStatsRepository.save(entity);
    }
}
