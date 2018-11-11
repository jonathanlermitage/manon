package manon;

import lombok.RequiredArgsConstructor;
import manon.app.config.Cfg;
import manon.app.trace.service.AppTraceService;
import manon.user.err.UserExistsException;
import manon.user.service.RegistrationService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

import static manon.app.trace.model.AppTraceEvent.APP_START;
import static manon.app.trace.model.AppTraceLevel.INFO;

@SpringBootApplication
@EnableMongoAuditing
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class Application extends SpringBootServletInitializer {
    
    private final Cfg cfg;
    private final AppTraceService appTraceService;
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
        String initAppEvent = String.format("jvm: [%s %s %s %s], os: [%s], file encoding: [%s], admin username: [%s], ManonConfig: [%s]",
            System.getProperty("java.version"),
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"),
            System.getProperty("java.vm.vendor"),
            System.getProperty("os.name"),
            System.getProperty("file.encoding"),
            registrationService.ensureAdmin().getUsername(),
            cfg);
        appTraceService.log(INFO, APP_START, initAppEvent);
    }
}
