package manon.repository.app;

import manon.document.app.AppTrace;
import manon.model.app.AppTraceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTraceRepository extends JpaRepository<AppTrace, Long> {
    
    long countByAppId(String appId);
    
    void deleteByAppIdAndEvent(String appId, AppTraceEvent event);
    
    long countByAppIdAndEvent(String appId, AppTraceEvent event);
}
