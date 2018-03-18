package manon.app.cache;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static manon.app.config.SpringProfiles.REDIS_CACHE;

@Configuration
@Profile(REDIS_CACHE)
public class RedisCacheConfig extends CachingConfigurerSupport {
    
    @Bean
    public RedisCacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .prefixKeysWith("manon_");
        return RedisCacheManager.builder(redisTemplate.getConnectionFactory())
                .cacheDefaults(config)
                .build();
    }
}
