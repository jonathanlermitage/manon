package manon.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChunkFlushListener extends ChunkListenerSupport {
    
    @PersistenceContext
    private final EntityManager em;
    
    @Override
    public void afterChunk(ChunkContext context) {
        super.afterChunk(context);
        em.flush();
        em.clear();
        log.debug("flushed pending modifications in step {}, job {}",
            context.getStepContext().getStepName(),
            context.getStepContext().getJobName());
    }
}
