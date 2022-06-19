package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.UserSnapshotEntity;
import manon.repository.user.UserSnapshotRepository;
import manon.service.user.UserSnapshotService;
import manon.util.ExistForTesting;
import manon.util.Tools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    public void keepRecent(Duration maxAge) {
        userSnapshotRepository.deleteAllByCreationDateBeforeOrCreationDateAfter(Tools.startOfDay().minus(maxAge), endOfDay());
    }

    @Override
    @ExistForTesting
    public long count() {
        return userSnapshotRepository.count();
    }

    @Override
    @ExistForTesting
    public Optional<UserSnapshotEntity> findOne(long id) {
        return userSnapshotRepository.findById(id);
    }

    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public List<UserSnapshotEntity> findAll() {
        return userSnapshotRepository.findAll();
    }

    @Override
    public UserSnapshotEntity persist(UserSnapshotEntity entity) {
        return userSnapshotRepository.persist(entity);
    }

    @Override
    @ExistForTesting
    public void persistAll(Iterable<UserSnapshotEntity> entities) {
        userSnapshotRepository.persistAll(entities);
    }

    @Override
    @ExistForTesting
    public void updateAll(Iterable<UserSnapshotEntity> entities) {
        userSnapshotRepository.updateAll(entities);
    }

    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public void deleteAll() {
        userSnapshotRepository.deleteAll();
    }
}
