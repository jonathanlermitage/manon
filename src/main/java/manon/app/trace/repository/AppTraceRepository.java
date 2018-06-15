package manon.app.trace.repository;

import manon.app.trace.document.AppTrace;
import manon.app.trace.model.AppTraceEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AppTraceRepository extends ReactiveMongoRepository<AppTrace, String> {
    
    Mono<Long> countByAppId(String appId);
    
    Mono<AppTrace> deleteByAppIdAndEvent(String appId, AppTraceEvent event);
    
    Mono<Long> countByAppIdAndEvent(String appId, AppTraceEvent event);
}
