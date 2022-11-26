package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.UserStatsEntity;
import manon.repository.user.UserStatsRepository;
import manon.service.user.UserStatsService;
import manon.util.ExistForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStatsServiceImpl implements UserStatsService {

    private final UserStatsRepository userStatsRepository;

    @Override
    public void persist(UserStatsEntity entity) {
        userStatsRepository.save(entity);
    }

    @Override
    @ExistForTesting(why = {"UserSnapshotJobConfigIntegrationTest", "UserStatsServiceIntegrationTest"})
    public List<UserStatsEntity> findAll() {
        return userStatsRepository.findAll();
    }

    @Override
    @ExistForTesting(why = "UserStatsServiceIntegrationTest")
    public void persistAll(Iterable<UserStatsEntity> entities) {
        userStatsRepository.saveAll(entities);
    }

    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public void deleteAll() {
        userStatsRepository.deleteAll();
    }
}
