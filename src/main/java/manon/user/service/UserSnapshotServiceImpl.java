package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.UserSnapshot;
import manon.user.repository.UserSnapshotRepository;
import manon.util.Tools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
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
        Calendar maxOldDate = Tools.today(0, 0, 0, 0);
        maxOldDate.add(Calendar.DAY_OF_MONTH, maxAgeInDays * -1);
        userSnapshotRepository.deleteAllByCreationDateBeforeOrCreationDateAfter(maxOldDate.getTime(), endOfDay());
    }
    
    @Override
    public void save(Iterable<UserSnapshot> entities) {
        userSnapshotRepository.saveAll(entities);
    }
}
