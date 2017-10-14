package manon.dashboard.service;

import manon.dashboard.document.ProfileSnapshot;

import java.util.Optional;

public interface ProfileSnapshotService {
    
    Optional<ProfileSnapshot> findOne(String id);
    
    long count();
    
    /** Count Profile snapshots created today. */
    long countToday();
    
    /** Delete Profile snapshots created today. */
    void deleteToday();
    
    /** Keep recent Profile snapshots only.
     * Snapshots older than {@code maxAgeInDays} days are deleted, in addition to future ones. */
    void keepRecent(int maxAgeInDays);
    
    void save(Iterable<ProfileSnapshot> entities);
}
