package manon;

import lombok.extern.slf4j.Slf4j;
import manon.app.management.service.TraceService;
import manon.user.UserExistsException;
import manon.user.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static manon.app.config.SpringProfiles.METRICS;
import static manon.app.management.document.AppTrace.CAT.APP_START;
import static manon.app.management.document.AppTrace.CAT.APP_STOP;
import static manon.app.management.document.AppTrace.CAT.PERFORMANCE_STATS;
import static manon.app.management.document.AppTrace.LVL.INFO;
import static manon.app.management.document.AppTrace.LVL.WARN;
import static manon.util.MethodExecutionTimeRecorder.showAllStats;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@Slf4j
public class Application extends SpringBootServletInitializer {
    
    private final UserAdminService userAdminService;
    private final TraceService traceService;
    private final Environment env;
    
    private long startUpTime = currentTimeMillis();
    
    @Autowired
    public Application(UserAdminService userAdminService, TraceService traceService, Environment env) {
        this.userAdminService = userAdminService;
        this.traceService = traceService;
        this.env = env;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.web(WebApplicationType.SERVLET);
        return application.sources(Application.class);
    }
    
    @PostConstruct
    public void initApp() throws UserExistsException {
        String initAppEvent = "Admin username is " + userAdminService.ensureAdmin().getUsername();
        traceService.log(INFO, APP_START, initAppEvent);
    }
    
    @PreDestroy
    public void destroy() {
        if (asList(env.getActiveProfiles()).contains(METRICS)) {
            traceService.log(INFO, PERFORMANCE_STATS, "\n" + showAllStats());
        }
        String destroyEvent = format("Application was alive during %ss (since %s)",
                (currentTimeMillis() - startUpTime) / 1_000,
                new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(new Date(startUpTime)));
        traceService.log(WARN, APP_STOP, destroyEvent);
    }
}
