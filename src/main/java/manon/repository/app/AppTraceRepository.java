package manon.repository.app;

import manon.document.app.AppTrace;
import manon.model.app.AppTraceEvent;
import manon.util.ExistForTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTraceRepository extends JpaRepository<AppTrace, Long> {
    
    void deleteByAppIdAndEvent(String appId, AppTraceEvent event);
    
    @ExistForTesting(why = "AppTraceServiceIntegrationTest")
    long countByAppId(String appId);
    
    @ExistForTesting(why = "AppTraceServiceIntegrationTest")
    long countByAppIdAndEvent(String appId, AppTraceEvent event);
}
