package manon.batch.listener;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChunkFlushListener implements ChunkListener {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public void afterChunk(@NotNull ChunkContext context) {
        em.flush();
        em.clear();
        log.debug("flushed pending modifications in step {}, job {}",
            context.getStepContext().getStepName(),
            context.getStepContext().getJobName());
    }
}
