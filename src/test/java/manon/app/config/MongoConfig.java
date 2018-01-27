package manon.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MongoConfig {
    
    @Value("${manon.mongodb.version}")
    private String mongodbVersion;
    
    @Bean
    @Primary
    public EmbeddedMongoProperties getEmbeddedMongoProperties() {
        EmbeddedMongoProperties embeddedMongoProperties = new EmbeddedMongoProperties();
        embeddedMongoProperties.setVersion(mongodbVersion);
        return embeddedMongoProperties;
    }
}
