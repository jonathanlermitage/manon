package manon;

import ch.qos.logback.classic.LoggerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.Cfg;
import manon.service.user.RegistrationService;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableAsync
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = "manon.repository")
@EntityScan(basePackages = "manon.document")
@EnableScheduling
@EnableRetry
@RequiredArgsConstructor
@Slf4j
public class Application extends SpringBootServletInitializer {

    private final Cfg cfg;
    private final RegistrationService registrationService;

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(Application.class);
        SpringApplication buildapp = app.build();
        buildapp.addListeners(new ApplicationPidFileWriter());
        buildapp.addListeners((ApplicationListener<ContextClosedEvent>) event -> {
            log.info("Shutdown process initiated...");
            // shutdown the Logback’s working thread gracefully
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.stop();
        });
        ConfigurableApplicationContext ctx = app.run(args);
        if (log.isDebugEnabled()) {
            List<String> autoconfig = Stream.of(ctx.getBeanDefinitionNames())
                .filter(s -> s.startsWith("org.springframework") && s.contains(".autoconfigure."))
                .sorted()
                .collect(Collectors.toList());
            log.debug("Loaded autoconfig: " + autoconfig);
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.web(WebApplicationType.SERVLET).sources(Application.class);
    }

    @PostConstruct
    public void startupHook() {
        log.info("jvm: [{} {} {} {}], os: [{}], file encoding: [{}], admin username: [{}], actuator username: [{}], Cfg: [{}]",
            System.getProperty("java.version"),
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"),
            System.getProperty("java.vm.vendor"),
            System.getProperty("os.name"),
            System.getProperty("file.encoding"),
            registrationService.ensureAdmin().getUsername(),
            registrationService.ensureActuator().getUsername(),
            cfg);
    }
}
