package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.UserSnapshot;
import manon.repository.user.UserSnapshotRepository;
import manon.service.user.UserSnapshotService;
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
    public Optional<UserSnapshot> findOne(long id) {
        return userSnapshotRepository.findById(id);
    }
    
    @Override
    public List<UserSnapshot> findAllByUserId(long userId) {
        return userSnapshotRepository.findAllByUserId(userId);
    }
    
    @Override
    public long count() {
        return userSnapshotRepository.count();
    }
    
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
    public void save(Iterable<UserSnapshot> entities) {
        userSnapshotRepository.saveAll(entities);
    }
}
