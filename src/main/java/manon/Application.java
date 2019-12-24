package manon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.Cfg;
import manon.service.user.RegistrationService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
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
        app.build().addListeners(new ApplicationPidFileWriter());
        ConfigurableApplicationContext ctx = app.run(args);
        if (log.isDebugEnabled()) {
            Stream.of(ctx.getBeanDefinitionNames())
                .filter(s -> s.startsWith("org.springframework") && s.contains(".autoconfigure."))
                .sorted()
                .forEach(s -> log.debug("Loaded autoconfig: " + s));
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
