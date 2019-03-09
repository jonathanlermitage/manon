package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.service.app.InfoService;
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
