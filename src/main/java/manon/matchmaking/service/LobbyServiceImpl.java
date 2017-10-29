package manon.matchmaking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbySolo;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.document.TeamInvitation;
import manon.matchmaking.repository.LobbySoloRepository;
import manon.matchmaking.repository.LobbyTeamRepository;
import manon.matchmaking.repository.TeamInvitationRepository;
import manon.profile.ProfileNotFoundException;
import manon.profile.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service("LobbyService")
@Slf4j
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {
    
    private final LobbySoloRepository lobbySoloRepository;
    private final LobbyTeamRepository lobbyTeamRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final ProfileService profileService;
    
    @Override
    public void flush() {
        lobbySoloRepository.deleteAll();
        lobbyTeamRepository.deleteAll();
    }
    
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
        lobbySoloRepository.removeByProfileId(profileId);
        lobbyTeamRepository.findByProfileIds(profileId).ifPresent(team -> {
            team = lobbyTeamRepository.removeProfileId(team.getId(), profileId);
            if (team.getProfileIds().isEmpty()) {
                lobbyTeamRepository.delete(team);
            } else if (team.getLeader().equals(profileId)) { // promote new leader
                lobbyTeamRepository.setLeader(team.getId(), team.getProfileIds().get(0));
            }
        });
    }
    
    @Override
    public LobbyTeam createTeamAndEnter(String profileId, LobbyLeagueEnum league) {
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
    public TeamInvitation inviteToTeam(String profileId, String profileIdToInvite)
            throws TeamNotFoundException, TeamInvitationException, ProfileNotFoundException {
        profileService.ensureExist(profileIdToInvite);
        if (profileId.equals(profileIdToInvite)) {
            throw new TeamInvitationException(TeamInvitationException.Cause.INVITE_ITSELF);
        }
        LobbyTeam originatorLobbyTeam = getTeam(profileId);
        Optional<TeamInvitation> invitation = teamInvitationRepository.findByProfileIdAndTeamId(profileIdToInvite, originatorLobbyTeam.getId());
        if (!invitation.isPresent()) {
            invitation = Optional.of(TeamInvitation.builder()
                    .profileId(profileIdToInvite)
                    .teamId(originatorLobbyTeam.getId())
                    .build());
            teamInvitationRepository.save(invitation.get());
        }
        return invitation.get();
    }
    
    @Override
    public List<TeamInvitation> getTeamInvitations(String profileId) {
        return teamInvitationRepository.findByProfileId(profileId);
    }
    
    @Override
    public LobbyTeam acceptTeamInvitation(String profileId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException {
        TeamInvitation invitation = teamInvitationRepository.findOne(invitationId);
        if (invitation == null || !profileId.equals(invitation.getProfileId())) {
            throw new TeamInvitationNotFoundException(profileId, invitationId);
        }
        quit(profileId);
        LobbyTeam team = lobbyTeamRepository.findById(invitation.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException(invitation.getTeamId()));
        if (team.getProfileIds().size() >= team.getMaxSize()) {
            throw new TeamFullException(invitation.getTeamId());
        }
        team = lobbyTeamRepository.addProfileId(invitation.getTeamId(), profileId);
        teamInvitationRepository.delete(invitationId);
        return team;
    }
    
    @Override
    public LobbyTeam getTeam(String profileId)
            throws TeamNotFoundException {
        return lobbyTeamRepository.findByProfileIds(profileId).orElseThrow(() -> new TeamNotFoundException(profileId));
    }
    
    @Override
    public LobbyTeam markTeamReady(String profileId, boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException {
        LobbyTeam team = getTeam(profileId);
        checkIsTeamLeader(profileId, team);
        lobbyTeamRepository.setReady(team.getId(), ready);
        return team.toBuilder().ready(ready).build();
    }
    
    @Override
    public LobbyTeam setTeamLeader(String profileId, String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        LobbyTeam team = getTeam(profileId);
        checkIsTeamLeader(profileId, team);
        checkIsTeamMember(newLeaderProfileId, team);
        lobbyTeamRepository.setLeader(team.getId(), newLeaderProfileId);
        return team.toBuilder().leader(newLeaderProfileId).build();
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
