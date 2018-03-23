package manon.app.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static manon.app.config.SpringProfiles.EMBEDDED_CACHE;

@Configuration
@Profile(EMBEDDED_CACHE)
public class EmbeddedCacheConfig extends CachingConfigurerSupport {
    
    @NotNull
    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
