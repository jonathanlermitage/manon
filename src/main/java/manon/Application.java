package manon;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.service.app.AppTraceService;
import manon.service.user.RegistrationService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static manon.model.app.AppTraceEvent.APP_START;
import static manon.model.app.AppTraceEvent.APP_STOP;
import static manon.model.app.AppTraceLevel.WARN;

@SpringBootApplication
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = "manon.repository")
@EntityScan(basePackages = "manon.document")
@EnableTransactionManagement
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
    public void startupHook() {
        String initAppEvent = String.format("jvm: [%s %s %s %s], os: [%s], file encoding: [%s], " +
                "admin username: [%s], actuator username: [%s], dev username: [%s], Cfg: [%s]",
            System.getProperty("java.version"),
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"),
            System.getProperty("java.vm.vendor"),
            System.getProperty("os.name"),
            System.getProperty("file.encoding"),
            registrationService.ensureAdmin().getUsername(),
            registrationService.ensureActuator().getUsername(),
            registrationService.ensureDev().getUsername(),
            cfg);
        appTraceService.log(WARN, APP_START, initAppEvent);
    }
    
    @PreDestroy
    public void shutdownHook() {
        appTraceService.log(WARN, APP_STOP);
    }
}
