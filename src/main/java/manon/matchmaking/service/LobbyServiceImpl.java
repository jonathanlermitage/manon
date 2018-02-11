package manon.matchmaking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbySolo;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.document.TeamInvitation;
import manon.matchmaking.model.LobbyStatus;
import manon.matchmaking.repository.LobbySoloRepository;
import manon.matchmaking.repository.LobbyTeamRepository;
import manon.matchmaking.repository.TeamInvitationRepository;
import manon.user.UserNotFoundException;
import manon.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {
    
    private final LobbySoloRepository lobbySoloRepository;
    private final LobbyTeamRepository lobbyTeamRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final UserService userService;
    
    @Override
    public void flush() {
        lobbySoloRepository.deleteAll();
        lobbyTeamRepository.deleteAll();
    }
    
    @Override
    public LobbyStatus getStatus(String userId) {
        Optional<LobbySolo> solo = lobbySoloRepository.findByUserId(userId);
        if (solo.isPresent()) {
            return LobbyStatus.builder().lobbySolo(solo.get()).build();
        }
        Optional<LobbyTeam> team = lobbyTeamRepository.findByUserIds(userId);
        if (team.isPresent()) {
            return LobbyStatus.builder().lobbyTeam(team.get()).build();
        }
        return LobbyStatus.EMPTY;
    }
    
    @Override
    public void enter(String userId, LobbyLeagueEnum league) {
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
        lobbyTeamRepository.findByUserIds(userId).ifPresent(team -> {
            team = lobbyTeamRepository.removeUserId(team.getId(), userId);
            if (team.getUserIds().isEmpty()) {
                lobbyTeamRepository.delete(team);
            } else if (team.getLeader().equals(userId)) { // promote new leader
                lobbyTeamRepository.setLeader(team.getId(), team.getUserIds().get(0));
            }
        });
    }
    
    @Override
    public LobbyTeam createTeamAndEnter(String userId, LobbyLeagueEnum league) {
        quit(userId);
        LobbyTeam team = LobbyTeam.builder()
                .league(league)
                .userIds(List.of(userId))
                .leader(userId)
                .build();
        lobbyTeamRepository.save(team);
        return team;
    }
    
    @Override
    public TeamInvitation inviteToTeam(String userId, String userIdToInvite)
            throws TeamNotFoundException, TeamInvitationException, UserNotFoundException {
        userService.ensureExist(userIdToInvite);
        if (userId.equals(userIdToInvite)) {
            throw new TeamInvitationException(TeamInvitationException.Cause.INVITE_ITSELF);
        }
        LobbyTeam originatorLobbyTeam = getTeam(userId);
        Optional<TeamInvitation> invitation = teamInvitationRepository.findByUserIdAndTeamId(userIdToInvite, originatorLobbyTeam.getId());
        if (!invitation.isPresent()) {
            return teamInvitationRepository.save(TeamInvitation.builder()
                    .userId(userIdToInvite)
                    .teamId(originatorLobbyTeam.getId())
                    .build());
        }
        return invitation.get();
    }
    
    @Override
    public List<TeamInvitation> getTeamInvitations(String userId) {
        return teamInvitationRepository.findByUserId(userId);
    }
    
    @Override
    public LobbyTeam acceptTeamInvitation(String userId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException {
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId).orElseThrow(() -> new TeamInvitationNotFoundException(userId, invitationId));
        if (!userId.equals(invitation.getUserId())) {
            throw new TeamInvitationNotFoundException(userId, invitationId);
        }
        quit(userId);
        LobbyTeam team = lobbyTeamRepository.findById(invitation.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException(invitation.getTeamId()));
        if (team.getUserIds().size() >= team.getMaxSize()) {
            throw new TeamFullException(invitation.getTeamId());
        }
        team = lobbyTeamRepository.addUserId(invitation.getTeamId(), userId);
        teamInvitationRepository.deleteById(invitationId);
        return team;
    }
    
    @Override
    public LobbyTeam getTeam(String userId)
            throws TeamNotFoundException {
        return lobbyTeamRepository.findByUserIds(userId).orElseThrow(() -> new TeamNotFoundException(userId));
    }
    
    @Override
    public LobbyTeam markTeamReady(String userId, boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException {
        LobbyTeam team = getTeam(userId);
        checkIsTeamLeader(userId, team);
        lobbyTeamRepository.setReady(team.getId(), ready);
        return team.toBuilder().ready(ready).build();
    }
    
    @Override
    public LobbyTeam setTeamLeader(String userId, String newLeaderUserId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        LobbyTeam team = getTeam(userId);
        checkIsTeamLeader(userId, team);
        checkIsTeamMember(newLeaderUserId, team);
        lobbyTeamRepository.setLeader(team.getId(), newLeaderUserId);
        return team.toBuilder().leader(newLeaderUserId).build();
    }
    
    /** Fail if user is not team leader. */
    private void checkIsTeamLeader(String userId, LobbyTeam team)
            throws TeamLeaderOnlyException {
        if (!Objects.equals(team.getLeader(), userId)) {
            throw new TeamLeaderOnlyException(userId);
        }
    }
    
    /** Fail is user is not a team member. */
    private void checkIsTeamMember(String userId, LobbyTeam team)
            throws TeamMemberNotFoundException {
        if (!team.getUserIds().contains(userId)) {
            throw new TeamMemberNotFoundException(userId);
        }
    }
}
