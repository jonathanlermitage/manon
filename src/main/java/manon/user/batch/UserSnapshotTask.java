package manon.user.batch;

import lombok.RequiredArgsConstructor;
import manon.app.batch.listener.TaskListener;
import manon.user.document.User;
import manon.user.document.UserSnapshot;
import manon.user.document.UserStats;
import manon.user.service.UserSnapshotService;
import manon.user.service.UserStatsService;
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
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

/** Keep recent user snapshots, then create snapshot of all existing users. */
@Configuration
@RequiredArgsConstructor
public class UserSnapshotTask {
    
    public static final String JOB_NAME = "userSnapshotJob";
    public static final String JOB_STEP0_KEEP_RECENT_NAME = "userSnapshotJobStepKeepRecent";
    public static final String JOB_STEP1_SNAPSHOT_NAME = "userSnapshotJobStepSnapshot";
    public static final String JOB_STEP2_STATS_NAME = "userSnapshotJobStepStats";
    
    private final MongoTemplate mongoTemplate;
    private final JobBuilderFactory jbf;
    private final StepBuilderFactory sbf;
    private final TaskListener listener;
    private final UserSnapshotService userSnapshotService;
    private final UserStatsService userStatsService;
    
    @Value("${manon.batch.user-snapshot.chunk}")
    private int chunk;
    @Value("${manon.batch.user-snapshot.snapshot.max-age}")
    private int maxAge;
    
    @Bean
    @StepScope
    public MongoItemReader<User> reader() {
        MongoItemReader<User> reader = new MongoItemReader<>();
        reader.setCollection(User.class.getSimpleName());
        reader.setTargetType(User.class);
        reader.setTemplate(mongoTemplate);
        reader.setQuery("{}");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }
    
    @Bean
    @StepScope
    public MongoItemWriter<UserSnapshot> writer() {
        MongoItemWriter<UserSnapshot> writer = new MongoItemWriter<>();
        writer.setCollection(UserSnapshot.class.getSimpleName());
        writer.setTemplate(mongoTemplate);
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
            .<User, UserSnapshot>chunk(chunk)
            .reader(reader())
            .processor(processor())
            .writer(writer())
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
            .listener(listener)
            .start(userSnapshotJobStepKeepRecent())
            .next(userSnapshotJobStepSnapshot())
            .next(userSnapshotJobStepStats())
            .build();
    }
    
    private class FlushTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            userSnapshotService.deleteToday();
            userSnapshotService.keepRecent(maxAge);
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
    
    private class UserItemProcessor implements ItemProcessor<User, UserSnapshot> {
        @Override
        public UserSnapshot process(User item) {
            return UserSnapshot.builder()
                .user(item)
                .build();
        }
    }
}
