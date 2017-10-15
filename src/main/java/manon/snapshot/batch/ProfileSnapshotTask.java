package manon.snapshot.batch;

import manon.snapshot.document.ProfileSnapshot;
import manon.snapshot.service.ProfileSnapshotService;
import manon.profile.document.Profile;
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

import java.util.Collections;

/** Keep recent Profile snapshots, too old and recent ones are deleted, then create snapshot of all existing Profiles. */
@Configuration
@EnableBatchProcessing
public class ProfileSnapshotTask {
    
    public static final String JOB_NAME = "profileSnapshotJob";
    public static final String JOB_STEP0_NAME = "profileSnapshotJobStepFlush";
    public static final String JOB_STEP1_NAME = "profileSnapshotJobStepSnapshot";
    
    private final MongoTemplate mongoTemplate;
    private final JobBuilderFactory jbf;
    private final StepBuilderFactory sbf;
    private final ProfileSnapshotTaskListener listener;
    private final ProfileSnapshotService profileSnapshotService;
    
    @Value("${manon.batch.ProfileSnapshotTask.profile.chunk}")
    private Integer chunk;
    @Value("${manon.batch.ProfileSnapshotTask.profileSnapshot.maxAge}")
    private Integer maxAge;
    
    @Autowired
    public ProfileSnapshotTask(MongoTemplate mongoTemplate, JobBuilderFactory jfb, StepBuilderFactory sbf,
                               ProfileSnapshotTaskListener listener, ProfileSnapshotService profileSnapshotService) {
        this.mongoTemplate = mongoTemplate;
        this.jbf = jfb;
        this.sbf = sbf;
        this.listener = listener;
        this.profileSnapshotService = profileSnapshotService;
    }
    
    @Bean
    @StepScope
    public MongoItemReader<Profile> reader() throws Exception {
        MongoItemReader<Profile> reader = new MongoItemReader<>();
        reader.setCollection(Profile.class.getSimpleName());
        reader.setTargetType(Profile.class);
        reader.setTemplate(mongoTemplate);
        reader.setQuery("{}");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }
    
    @Bean
    @StepScope
    public MongoItemWriter<ProfileSnapshot> writer() {
        MongoItemWriter<ProfileSnapshot> writer = new MongoItemWriter<>();
        writer.setCollection(ProfileSnapshot.class.getSimpleName());
        writer.setTemplate(mongoTemplate);
        return writer;
    }
    
    @Bean
    public ItemProcessor<Profile, ProfileSnapshot> processor() {
        return new ProfileItemProcessor();
    }
    
    @Bean(JOB_STEP0_NAME)
    public Step profileSnapshotJobStepFlush() throws Exception {
        return sbf.get(JOB_STEP1_NAME)
                .tasklet(new FlushTasklet())
                .build();
    }
    
    @Bean(JOB_STEP1_NAME)
    public Step profileSnapshotJobStepSnapshot() throws Exception {
        return sbf.get(JOB_STEP1_NAME)
                .<Profile, ProfileSnapshot>chunk(chunk)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    
    @Bean(JOB_NAME)
    Job profileSnapshotJob() throws Exception {
        return jbf.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(profileSnapshotJobStepFlush())
                .next(profileSnapshotJobStepSnapshot())
                .build();
    }
    
    private class FlushTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            profileSnapshotService.deleteToday();
            profileSnapshotService.keepRecent(maxAge);
            return RepeatStatus.FINISHED;
        }
    }
    
    private class ProfileItemProcessor implements ItemProcessor<Profile, ProfileSnapshot> {
        @Override
        public ProfileSnapshot process(Profile item) throws Exception {
            return ProfileSnapshot.builder()
                    .profile(item)
                    .build();
        }
    }
}
