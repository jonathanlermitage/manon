package manon.app.sys.service;

import lombok.RequiredArgsConstructor;
import manon.app.config.Cfg;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    
    private final Cfg cfg;
    
    @Override
    public String getAppVersion() {
        return cfg.getVersion();
    }
}
