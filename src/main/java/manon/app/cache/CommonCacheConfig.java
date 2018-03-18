package manon.app.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonCacheConfig {
    
    /** Key generator name: generate a unique key of the class name, the method name, and all method parameters appended. */
    public static final String KEY_GENERATOR_FULL = "KEY_GENERATOR_FULL";
    
    /** Generate a unique key of the class name, the method name, and all method parameters appended. */
    @NotNull
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
