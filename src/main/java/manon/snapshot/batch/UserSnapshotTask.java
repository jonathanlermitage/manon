package manon.snapshot.batch;

import manon.snapshot.document.UserSnapshot;
import manon.snapshot.document.UsersStats;
import manon.snapshot.service.UserSnapshotService;
import manon.snapshot.service.UserStatsService;
import manon.user.document.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import static java.util.Collections.singletonMap;

/** Keep recent User snapshots, too old and recent ones are deleted, then create snapshot of all existing SpringProfiles. */
@Configuration
@EnableBatchProcessing
public class UserSnapshotTask {
    
    public static final String JOB_NAME = "userSnapshotJob";
    public static final String JOB_STEP0_NAME = "userSnapshotJobStepFlush";
    public static final String JOB_STEP1_NAME = "userSnapshotJobStepSnapshot";
    public static final String JOB_STEP2_NAME = "userSnapshotJobStepStats";
    
    private final MongoTemplate mongoTemplate;
    private final JobBuilderFactory jbf;
    private final StepBuilderFactory sbf;
    private final UserSnapshotTaskListener listener;
    private final UserSnapshotService userSnapshotService;
    private final UserStatsService userStatsService;
    
    @Value("${manon.batch.UserSnapshotTask.user.chunk}")
    private Integer chunk;
    @Value("${manon.batch.UserSnapshotTask.userSnapshot.maxAge}")
    private Integer maxAge;
    
    @Autowired
    public UserSnapshotTask(MongoTemplate mongoTemplate, JobBuilderFactory jfb, StepBuilderFactory sbf,
                            UserSnapshotTaskListener listener,
                            UserSnapshotService userSnapshotService,
                            UserStatsService userStatsService) {
        this.mongoTemplate = mongoTemplate;
        this.jbf = jfb;
        this.sbf = sbf;
        this.listener = listener;
        this.userSnapshotService = userSnapshotService;
        this.userStatsService = userStatsService;
    }
    
    @Bean
    @StepScope
    public MongoItemReader<User> reader() {
        MongoItemReader<User> reader = new MongoItemReader<>();
        reader.setCollection(User.class.getSimpleName());
        reader.setTargetType(User.class);
        reader.setTemplate(mongoTemplate);
        reader.setQuery("{}");
        reader.setSort(singletonMap("id", Sort.Direction.ASC));
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
    
    @Bean(JOB_STEP0_NAME)
    public Step userSnapshotJobStepFlush() {
        return sbf.get(JOB_STEP1_NAME)
                .tasklet(new FlushTasklet())
                .build();
    }
    
    @Bean(JOB_STEP1_NAME)
    public Step userSnapshotJobStepSnapshot() {
        return sbf.get(JOB_STEP1_NAME)
                .<User, UserSnapshot>chunk(chunk)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    
    @Bean(JOB_STEP2_NAME)
    public Step userSnapshotJobStepStats() {
        return sbf.get(JOB_STEP1_NAME)
                .tasklet(new StatsTasklet())
                .build();
    }
    
    @Bean(JOB_NAME)
    Job userSnapshotJob() {
        return jbf.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(userSnapshotJobStepFlush())
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
            userStatsService.save(UsersStats.builder().nbUsers(userSnapshotService.countToday()).build());
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
