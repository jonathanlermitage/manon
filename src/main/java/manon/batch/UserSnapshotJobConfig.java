package manon.batch;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.batch.listener.ChunkFlushListener;
import manon.batch.listener.JobListener;
import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.document.user.UserStatsEntity;
import manon.mapper.user.UserMapper;
import manon.repository.user.UserRepository;
import manon.repository.user.UserSnapshotRepository;
import manon.service.user.UserSnapshotService;
import manon.service.user.UserStatsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Map;

/** Keep recent user snapshots, then create snapshot of all existing users. */
@Configuration
@RequiredArgsConstructor
public class UserSnapshotJobConfig {

    public static final String JOB_NAME = "userSnapshotJob";
    public static final String JOB_STEP0_KEEP_RECENT_NAME = "userSnapshotJobStepKeepRecent";
    public static final String JOB_STEP1_SNAPSHOT_NAME = "userSnapshotJobStepSnapshot";
    public static final String JOB_STEP2_STATS_NAME = "userSnapshotJobStepStats";

    private static final Map<String, Sort.Direction> SORT = Collections.singletonMap("id", Sort.Direction.ASC);

    private final Cfg cfg;
    private final JobBuilderFactory jbf;
    private final StepBuilderFactory sbf;
    private final JobListener jobListener;
    private final ChunkFlushListener chunkFlushListener;
    private final UserRepository userRepository;
    private final UserSnapshotRepository userSnapshotRepository;
    private final UserSnapshotService userSnapshotService;
    private final UserStatsService userStatsService;

    @Bean(JOB_NAME)
    Job userSnapshotJob() {
        return jbf.get(JOB_NAME)
            .incrementer(new RunIdIncrementer())
            .listener(jobListener)
            .start(userSnapshotJobStepKeepRecent())
            .next(userSnapshotJobStepSnapshot())
            .next(userSnapshotJobStepStats())
            .build();
    }

    //
    // userSnapshotJobStepKeepRecent
    //

    public Step userSnapshotJobStepKeepRecent() {
        return sbf.get(JOB_STEP0_KEEP_RECENT_NAME)
            .tasklet(new FlushTasklet())
            .build();
    }

    private class FlushTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) {
            userSnapshotService.deleteToday();
            userSnapshotService.keepRecent(cfg.getBatchUserSnapshotSnapshotMaxAge());
            return RepeatStatus.FINISHED;
        }
    }

    //
    // userSnapshotJobStepSnapshot
    //

    public Step userSnapshotJobStepSnapshot() {
        return sbf.get(JOB_STEP1_SNAPSHOT_NAME)
            .<UserEntity, UserSnapshotEntity>chunk(cfg.getBatchUserSnapshotChunk())
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .listener(chunkFlushListener)
            .build();
    }

    public RepositoryItemReader<UserEntity> reader() {
        RepositoryItemReader<UserEntity> reader = new RepositoryItemReader<>();
        reader.setRepository(userRepository);
        reader.setSort(SORT);
        reader.setMethodName("findAll");
        reader.setPageSize(cfg.getBatchUserSnapshotChunk());
        return reader;
    }

    public ItemProcessor<UserEntity, UserSnapshotEntity> processor() {
        return new UserItemProcessor();
    }

    private static class UserItemProcessor implements ItemProcessor<UserEntity, UserSnapshotEntity> {
        @Override
        public UserSnapshotEntity process(@NotNull UserEntity item) {
            return UserMapper.MAPPER.toUserSnapshotEntity(item);
        }
    }

    public RepositoryItemWriter<UserSnapshotEntity> writer() {
        RepositoryItemWriter<UserSnapshotEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(userSnapshotRepository);
        writer.setMethodName("save");
        return writer;
    }

    //
    // userSnapshotJobStepStats
    //

    public Step userSnapshotJobStepStats() {
        return sbf.get(JOB_STEP2_STATS_NAME)
            .tasklet(new StatsTasklet())
            .build();
    }

    private class StatsTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) {
            userStatsService.save(UserStatsEntity.builder().nbUsers(userSnapshotService.countToday()).build());
            return RepeatStatus.FINISHED;
        }
    }
}
