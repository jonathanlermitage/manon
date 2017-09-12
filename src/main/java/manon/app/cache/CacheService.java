package manon.app.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {
    
    private final RedisTemplate<String, String> redis;
    
    @Value("${spring.redis.database}")
    private String redisDatabase;
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    
    @Autowired
    public CacheService(RedisTemplate<String, String> redis) {
        this.redis = redis;
    }
    
    /** Delete all keys of the current Redis database cache. */
    public void flushDb() {
        redis.executePipelined((RedisCallback<Object>) connection -> {
            connection.flushDb();
            log.debug("flushed Redis database: {}@{}:{}", redisDatabase, redisHost, redisPort);
            return null;
        });
    }
}
