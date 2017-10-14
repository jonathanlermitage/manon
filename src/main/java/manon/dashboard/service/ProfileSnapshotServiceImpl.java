package manon.dashboard.service;

import lombok.RequiredArgsConstructor;
import manon.dashboard.document.ProfileSnapshot;
import manon.dashboard.repository.ProfileSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

import static manon.util.Tools.calendar;
import static manon.util.Tools.endOfDay;
import static manon.util.Tools.startOfDay;

@Service("ProfileSnapshotService")
@RequiredArgsConstructor
public class ProfileSnapshotServiceImpl implements ProfileSnapshotService {
    
    private final ProfileSnapshotRepository profileSnapshotRepository;
    
    @Override
    public Optional<ProfileSnapshot> findOne(String id) {
        return Optional.ofNullable(profileSnapshotRepository.findOne(id));
    }
    
    @Override
    public long count() {
        return profileSnapshotRepository.count();
    }
    
    @Override
    public long countToday() {
        return profileSnapshotRepository.countAllByCreationDateInRange(startOfDay(), endOfDay());
    }
    
    @Override
    public void deleteToday() {
        profileSnapshotRepository.deleteAllByCreationDateInRange(startOfDay(), endOfDay());
    }
    
    @Override
    public void keepRecent(int maxAgeInDays) {
        Calendar maxOldDate = calendar(0, 0, 0, 0);
        maxOldDate.add(Calendar.DAY_OF_MONTH, maxAgeInDays * -1);
        profileSnapshotRepository.deleteAllByCreationDateOutsideRange(maxOldDate.getTime(), endOfDay());
    }
    
    @Override
    public void save(Iterable<ProfileSnapshot> entities) {
        profileSnapshotRepository.save(entities);
    }
}
