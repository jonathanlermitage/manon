package manon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.info.service.InfoService;
import manon.app.trace.service.AppTraceService;
import manon.game.world.service.WorldService;
import manon.user.err.UserExistsException;
import manon.user.service.RegistrationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

import static manon.app.trace.model.AppTraceEvent.APP_START;
import static manon.app.trace.model.AppTraceLevel.INFO;

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@EnableCaching
@Slf4j
@RequiredArgsConstructor
public class Application extends SpringBootServletInitializer {
    
    private final AppTraceService appTraceService;
    private final InfoService infoService;
    private final WorldService worldService;
    private final RegistrationService registrationService;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.web(WebApplicationType.SERVLET).sources(Application.class);
    }
    
    @PostConstruct
    public void initApp() throws UserExistsException {
        infoService.evictCaches();
        worldService.evictCaches();
        String initAppEvent = "Admin username is " + registrationService.ensureAdmin().getUsername();
        appTraceService.log(INFO, APP_START, initAppEvent);
    }
}
