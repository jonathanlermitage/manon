package manon.app.config;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.app.Globals;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;

import static manon.app.Globals.Properties.CACHE_PROVIDER;
import static manon.app.Globals.Properties.CACHE_PROVIDER_REDIS;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = CACHE_PROVIDER, havingValue = CACHE_PROVIDER_REDIS)
@RequiredArgsConstructor
public class RedisCacheConfig extends CachingConfigurerSupport {

    private final Cfg cfg;

    @SuppressWarnings({"CollectionAddAllCanBeReplacedWithConstructor", "ConstantConditions"})
    @Bean
    public RedisCacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(cfg.getCacheRedisTtl())
            .prefixKeysWith("manon_");
        Set<String> cacheNames = new HashSet<>();
        cacheNames.addAll(Globals.CacheNames.allCaches());
        return RedisCacheManager.builder(redisTemplate.getConnectionFactory())
            .cacheDefaults(config)
            .initialCacheNames(cacheNames)
            .build();
    }
}
