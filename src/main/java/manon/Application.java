package manon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoAuditing
@EnableReactiveMongoRepositories
@EnableScheduling
@EnableWebFlux
@RequiredArgsConstructor
@Slf4j
public class Application {
    
    @Value("${version}")
    private String version;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @PostConstruct
    public void initApp() {
        String initAppEvent = String.format("App version %s, Java %s, %s %s by %s, on %s with %s file encoding",
                version,
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.vm.vendor"),
                System.getProperty("os.name"),
                System.getProperty("file.encoding"));
        log.info(initAppEvent);
    }
}
