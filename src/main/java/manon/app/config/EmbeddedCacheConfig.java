package manon.app.config;

import manon.app.Globals;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static manon.app.Globals.Properties.CACHE_PROVIDER;
import static manon.app.Globals.Properties.CACHE_PROVIDER_EMBEDDED;

@Configuration
@ConditionalOnProperty(name = CACHE_PROVIDER, havingValue = CACHE_PROVIDER_EMBEDDED)
@EnableCaching
public class EmbeddedCacheConfig extends CachingConfigurerSupport {
    
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Globals.CacheNames.ALL_CACHES());
        return cacheManager;
    }
}
