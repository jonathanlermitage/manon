package manon.matchmaking.service;

import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.repository.MatchMakingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/** Handle matchmaking. */
@Service("MatchMakingService")
@Slf4j
public class MatchMakingServiceImpl implements MatchMakingService {
    
    private final MatchMakingRepository matchMakingRepository;
    
    /** Start matchmaking job. See YAML configuration. */
    @Value("${manon.matchmaking.job.start}")
    private boolean startMatchmaking;
    
    /** Enable matchmaking job. See configuration from database. */
    private boolean enableMatchmaking;
    
    @Autowired
    public MatchMakingServiceImpl(MatchMakingRepository matchMakingRepository) {
        this.matchMakingRepository = matchMakingRepository;
    }
    
    @Override
    @PostConstruct
    public void prepare() {
        enableMatchmaking = true;
        if (startMatchmaking) {
            startMatchmaking();
        }
    }
    
    @Override
    public void enable(boolean enable) {
        enableMatchmaking = enable;
    }
    
    @Override
    public void startMatchmaking() {
        if (enableMatchmaking) {
            
            
            log.info("matchmaking job started");
        } else {
            log.info("matchmaking job not started (disabled on this server)");
        }
    }
}
