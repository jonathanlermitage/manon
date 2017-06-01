package manon.matchmaking.service;

import javax.annotation.PostConstruct;

public interface MatchMakingService {
    
    @PostConstruct
    void prepare();
    
    void enable(boolean enable);
    
    void startMatchmaking();
}
