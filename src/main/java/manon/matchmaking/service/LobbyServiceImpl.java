package manon.matchmaking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.document.LobbySolo;
import manon.matchmaking.model.LobbyLeague;
import manon.matchmaking.model.LobbyStatus;
import manon.matchmaking.repository.LobbySoloRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {
    
    private final LobbySoloRepository lobbySoloRepository;
    
    @Override
    public void flush() {
        lobbySoloRepository.deleteAll();
    }
    
    @Override
    public LobbyStatus getStatus(String userId) {
        Optional<LobbySolo> solo = lobbySoloRepository.findByUserId(userId);
        if (solo.isPresent()) {
            return LobbyStatus.builder().lobbySolo(solo.get()).build();
        }
        return LobbyStatus.EMPTY;
    }
    
    @Override
    public void enter(String userId, LobbyLeague league) {
        quit(userId);
        LobbySolo solo = LobbySolo.builder()
                .league(league)
                .userId(userId)
                .build();
        lobbySoloRepository.save(solo);
    }
    
    @Override
    public void quit(String userId) {
        lobbySoloRepository.removeByUserId(userId);
    }
}
