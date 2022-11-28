package manon.app.config;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.app.Globals;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static manon.app.Globals.Properties.CACHE_PROVIDER;
import static manon.app.Globals.Properties.CACHE_PROVIDER_REDIS;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = CACHE_PROVIDER, havingValue = CACHE_PROVIDER_REDIS)
@RequiredArgsConstructor
public class RedisCacheConfig implements CachingConfigurer {

    private final Cfg cfg;

    @Bean
    public RedisCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(cfg.getCacheRedisTtl())
            .prefixCacheNameWith("manon_");
        Set<String> cacheNames = new HashSet<>(Globals.CacheNames.allCaches());
        return RedisCacheManager.builder(Objects.requireNonNull(redisTemplate.getConnectionFactory()))
            .cacheDefaults(config)
            .initialCacheNames(cacheNames)
            .build();
    }
}
