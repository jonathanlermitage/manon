package manon.app.info.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:info.properties")
public class InfoServiceImpl implements InfoService {
    
    @Value("${version}")
    private String version;
    
    @Override
    public String getAppVersion() {
        return version;
    }
}
