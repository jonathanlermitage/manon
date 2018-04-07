package manon.app.info.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:info.properties")
public class InfoServiceImpl implements InfoService {
    
    public static final String CACHE_GET_APPVERSION = "CACHE_GET_APPVERSION";
    
    @Value("${version}")
    private String version;
    
    @CacheEvict(value = {CACHE_GET_APPVERSION}, allEntries = true)
    @Override
    public void evictCaches() {
        // action in annotation
    }
    
    @Cacheable(CACHE_GET_APPVERSION)
    @Override
    public String getAppVersion() {
        return version;
    }
}
