package manon.batch;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.batch.listener.ChunkFlushListener;
import manon.batch.listener.TaskListener;
import manon.document.user.User;
import manon.document.user.UserSnapshot;
import manon.document.user.UserStats;
import manon.repository.user.UserRepository;
import manon.repository.user.UserSnapshotRepository;
import manon.service.user.UserSnapshotService;
import manon.service.user.UserStatsService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
public class UserSnapshotTask {
    
    public static final String JOB_NAME = "userSnapshotJob";
    public static final String JOB_STEP0_KEEP_RECENT_NAME = "userSnapshotJobStepKeepRecent";
    public static final String JOB_STEP1_SNAPSHOT_NAME = "userSnapshotJobStepSnapshot";
    public static final String JOB_STEP2_STATS_NAME = "userSnapshotJobStepStats";
    
    private static final Map<String, Sort.Direction> SORT = Collections.singletonMap("id", Sort.Direction.ASC);
    
    private final Cfg cfg;
    private final JobBuilderFactory jbf;
    private final StepBuilderFactory sbf;
    private final TaskListener taskListener;
    private final ChunkFlushListener chunkFlushListener;
    private final UserRepository userRepository;
    private final UserSnapshotRepository userSnapshotRepository;
    private final UserSnapshotService userSnapshotService;
    private final UserStatsService userStatsService;
    
    @Bean
    @StepScope
    public RepositoryItemReader<User> reader() {
        RepositoryItemReader<User> reader = new RepositoryItemReader<>();
        reader.setRepository(userRepository);
        reader.setSort(SORT);
        reader.setMethodName("findAll");
        reader.setPageSize(cfg.getBatchUserSnapshotChunk());
        return reader;
    }
    
    @Bean
    @StepScope
    public RepositoryItemWriter<UserSnapshot> writer() {
        RepositoryItemWriter<UserSnapshot> writer = new RepositoryItemWriter<>();
        writer.setRepository(userSnapshotRepository);
        writer.setMethodName("save");
        return writer;
    }
    
    @Bean
    public ItemProcessor<User, UserSnapshot> processor() {
        return new UserItemProcessor();
    }
    
    @Bean(JOB_STEP0_KEEP_RECENT_NAME)
    public Step userSnapshotJobStepKeepRecent() {
        return sbf.get(JOB_STEP0_KEEP_RECENT_NAME)
            .tasklet(new FlushTasklet())
            .build();
    }
    
    @Bean(JOB_STEP1_SNAPSHOT_NAME)
    public Step userSnapshotJobStepSnapshot() {
        return sbf.get(JOB_STEP1_SNAPSHOT_NAME)
            .<User, UserSnapshot>chunk(cfg.getBatchUserSnapshotChunk())
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .listener(chunkFlushListener)
            .build();
    }
    
    @Bean(JOB_STEP2_STATS_NAME)
    public Step userSnapshotJobStepStats() {
        return sbf.get(JOB_STEP2_STATS_NAME)
            .tasklet(new StatsTasklet())
            .build();
    }
    
    @Bean(JOB_NAME)
    Job userSnapshotJob() {
        return jbf.get(JOB_NAME)
            .incrementer(new RunIdIncrementer())
            .listener(taskListener)
            .start(userSnapshotJobStepKeepRecent())
            .next(userSnapshotJobStepSnapshot())
            .next(userSnapshotJobStepStats())
            .build();
    }
    
    private class FlushTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            userSnapshotService.deleteToday();
            userSnapshotService.keepRecent(cfg.getBatchUserSnapshotSnapshotMaxAge());
            return RepeatStatus.FINISHED;
        }
    }
    
    private class StatsTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            userStatsService.save(UserStats.builder().nbUsers(userSnapshotService.countToday()).build());
            return RepeatStatus.FINISHED;
        }
    }
    
    private static class UserItemProcessor implements ItemProcessor<User, UserSnapshot> {
        @Override
        public UserSnapshot process(User item) {
            return UserSnapshot.from(item);
        }
    }
}