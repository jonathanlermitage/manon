package manon.matchmaking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbySolo;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.repository.LobbySoloRepository;
import manon.matchmaking.repository.LobbyTeamRepository;
import manon.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service("LobbyService")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LobbyServiceImpl implements LobbyService {
    
    private final LobbySoloRepository lobbySoloRepository;
    private final LobbyTeamRepository lobbyTeamRepository;
    private final ProfileService profileService;
    
    @Override
    public ProfileLobbyStatus getStatus(String profileId) {
        Optional<LobbySolo> solo = lobbySoloRepository.findByProfileId(profileId);
        if (solo.isPresent()) {
            return ProfileLobbyStatus.builder().lobbySolo(solo.get()).build();
        }
        Optional<LobbyTeam> team = lobbyTeamRepository.findByProfileIds(profileId);
        if (team.isPresent()) {
            return ProfileLobbyStatus.builder().lobbyTeam(team.get()).build();
        }
        return ProfileLobbyStatus.EMPTY;
    }
    
    @Override
    public void enter(String profileId, LobbyLeagueEnum league) {
        quit(profileId);
        LobbySolo solo = LobbySolo.builder()
                .league(league)
                .profileId(profileId)
                .skill(profileService.getSkill(profileId))
                .build();
        lobbySoloRepository.save(solo);
    }
    
    @Override
    public void quit(String profileId) {
        long nbProfileDeleted = lobbySoloRepository.removeByProfileId(profileId);
        if (nbProfileDeleted > 1) {
            log.warn("removed profile {} from solo lobby, but was present {} times (should be O or 1)",
                    profileId, nbProfileDeleted);
        }
        long nbProfileRemovedFromTeam = lobbyTeamRepository.unsetProfileId(profileId);
        if (nbProfileRemovedFromTeam > 1) {
            log.warn("removed profile {} from team lobby, but was present {} times (should be O or 1)",
                    profileId, nbProfileRemovedFromTeam);
        }
        if (nbProfileDeleted > 1 && nbProfileRemovedFromTeam > 1) {
            log.warn("removed profile {} from solo and team lobby, was present {} times in solo and {} times in team",
                    profileId, nbProfileDeleted, nbProfileRemovedFromTeam);
        }
    }
    
    @Override
    public LobbyTeam createTeam(String profileId, LobbyLeagueEnum league) {
        quit(profileId);
        LobbyTeam team = LobbyTeam.builder()
                .league(league)
                .profileIds(singletonList(profileId))
                .leader(profileId)
                .skill(profileService.getSkill(profileId))
                .build();
        lobbyTeamRepository.save(team);
        return team;
    }
    
    @Override
    public LobbyTeam enterIntoTeam(String profileId, String teamId)
            throws TeamNotFoundException, TeamFullException {
        // TODO
        return null;
    }
    
    @Override
    public LobbyTeam inviteToTeam(String profileId, String teamId)
            throws TeamNotFoundException {
        // TODO
        return null;
    }
    
    @Override
    public LobbyTeam acceptTeamInvitation(String profileId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException {
        quit(profileId);
        // TODO
        return null;
    }
    
    @Override
    public LobbyTeam getTeamByProfile(String profileId)
            throws TeamNotFoundException {
        Optional<LobbyTeam> team = lobbyTeamRepository.findByProfileIds(profileId);
        if (team.isPresent()) {
            return team.get();
        } else {
            throw new TeamNotFoundException(profileId);
        }
    }
    
    @Override
    public LobbyTeam markTeamReady(String profileId, boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException {
        LobbyTeam team = getTeamByProfile(profileId);
        checkIsTeamLeader(profileId, team);
        team = team.toBuilder().ready(ready).build();
        lobbyTeamRepository.save(team);
        return team;
    }
    
    @Override
    public LobbyTeam changeTeamLeader(String profileId, String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        LobbyTeam team = getTeamByProfile(profileId);
        checkIsTeamLeader(profileId, team);
        checkIsTeamMember(newLeaderProfileId, team);
        team = team.toBuilder().leader(newLeaderProfileId).build();
        lobbyTeamRepository.save(team);
        return team;
    }
    
    /** Fail if profile is not team leader. */
    private void checkIsTeamLeader(String profileId, LobbyTeam team)
            throws TeamLeaderOnlyException {
        if (!Objects.equals(team.getLeader(), profileId)) {
            throw new TeamLeaderOnlyException(profileId);
        }
    }
    
    /** Fail is profile is not a team member. */
    private void checkIsTeamMember(String profileId, LobbyTeam team)
            throws TeamMemberNotFoundException {
        if (!team.getProfileIds().contains(profileId)) {
            throw new TeamMemberNotFoundException(profileId);
        }
    }
}
