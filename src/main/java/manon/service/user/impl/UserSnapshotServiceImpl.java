package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.UserSnapshot;
import manon.repository.user.UserSnapshotRepository;
import manon.service.user.UserSnapshotService;
import manon.util.ExistForTesting;
import manon.util.Tools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static manon.util.Tools.endOfDay;
import static manon.util.Tools.startOfDay;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSnapshotServiceImpl implements UserSnapshotService {
    
    private final UserSnapshotRepository userSnapshotRepository;
    
    @Override
    public long countToday() {
        return userSnapshotRepository.countAllByCreationDateBetween(startOfDay(), endOfDay());
    }
    
    @Override
    public void deleteToday() {
        userSnapshotRepository.deleteAllByCreationDateBetween(startOfDay(), endOfDay());
    }
    
    @Override
    public void keepRecent(int maxAgeInDays) {
        userSnapshotRepository.deleteAllByCreationDateBeforeOrCreationDateAfter(Tools.startOfDay().minusDays(maxAgeInDays), endOfDay());
    }
    
    @Override
    @ExistForTesting
    public long count() {
        return userSnapshotRepository.count();
    }
    
    @Override
    @ExistForTesting
    public Optional<UserSnapshot> findOne(long id) {
        return userSnapshotRepository.findById(id);
    }
    
    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public List<UserSnapshot> findAll() {
        return userSnapshotRepository.findAll();
    }
    
    @Override
    public UserSnapshot save(UserSnapshot entity) {
        return userSnapshotRepository.save(entity);
    }
    
    @Override
    @ExistForTesting
    public void saveAll(Iterable<UserSnapshot> entities) {
        userSnapshotRepository.saveAll(entities);
    }
    
    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public void deleteAll() {
        userSnapshotRepository.deleteAll();
    }
}
