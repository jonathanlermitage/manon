package manon;

import lombok.extern.slf4j.Slf4j;
import manon.app.management.service.TraceService;
import manon.profile.ProfileNotFoundException;
import manon.user.UserExistsException;
import manon.user.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static manon.app.management.document.AppTrace.CAT.APP_START;
import static manon.app.management.document.AppTrace.CAT.APP_STOP;
import static manon.app.management.document.AppTrace.CAT.PERFORMANCE_STATS;
import static manon.app.management.document.AppTrace.TRACE_LEVEL.APP_LIFECYCLE;
import static manon.app.management.document.AppTrace.TRACE_LEVEL.INFO;
import static manon.util.MethodExecutionTimeRecorder.showAllStats;
import static manon.util.Tools.str;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@Slf4j
public class Application extends SpringBootServletInitializer {
    
    @Autowired
    private AdminService adminService;
    @Autowired
    private TraceService traceService;
    
    private long startUpTime = currentTimeMillis();
    
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.web(true);
        return application.sources(Application.class);
    }
    
    @PostConstruct
    public void initApp() throws IOException, UserExistsException, ProfileNotFoundException {
        log.warn("*** MANON APPLICATION START ***");
        String initAppEvent = str("Admin username is %s", adminService.ensureAdmin().getUsername());
        log.info(initAppEvent);
        traceService.log(APP_LIFECYCLE, APP_START, initAppEvent);
    }
    
    @PreDestroy
    public void destroy() {
        traceService.log(INFO, PERFORMANCE_STATS, showAllStats());
        String destroyEvent = str("Application was alive during %s seconds (started on %s)",
                (currentTimeMillis() - startUpTime) / 1_000,
                new Date(startUpTime).toString());
        traceService.log(APP_LIFECYCLE, APP_STOP, destroyEvent);
        log.warn(destroyEvent);
        log.warn("*** MANON APPLICATION STOP ***  ᶠᶸᶜᵏ♥ᵧₒᵤ");
    }
}
