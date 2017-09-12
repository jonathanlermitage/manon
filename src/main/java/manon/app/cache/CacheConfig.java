package manon.app.cache;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    
    /** Key generator name: generate a unique key of the class name, the method name, and all method parameters appended. */
    public static final String KEY_GENERATOR_FULL = "KEY_GENERATOR_FULL";
    
    @Bean
    public RedisCacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration, defaults to unlimited (0).
        cacheManager.setDefaultExpiration(3600);
        cacheManager.setLoadRemoteCachesOnStartup(false);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }
    
    /** Generate a unique key of the class name, the method name, and all method parameters appended. */
    @Bean(name = KEY_GENERATOR_FULL)
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append(method.getName());
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}
