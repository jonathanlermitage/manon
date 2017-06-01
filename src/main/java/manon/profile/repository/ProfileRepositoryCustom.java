package manon.profile.repository;

import manon.profile.ProfileFieldEnum;
import manon.profile.ProfileNotFoundException;
import manon.profile.ProfileStateEnum;

import java.util.Collection;

public interface ProfileRepositoryCustom {
    
    void updateField(String id, ProfileFieldEnum field, Object value) throws ProfileNotFoundException;
    
    /**
     * Add a friendship request between two profiles.
     * @param profileIdFrom id of profile that asks for friendship.
     * @param profileIdTo id of profile that is targeted.
     */
    void askFriendship(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException;
    
    /**
     * Create a friendship request between two profiles.
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that accepts friendship.
     */
    void acceptFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException;
    
    /**
     * Reject a friendship request between two profiles.
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that rejects friendship.
     */
    void rejectFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException;
    
    /**
     * cancel a friendship request between two profiles.
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that rejects friendship.
     */
    void cancelFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException;
    
    /**
     * Delete an existing friendship relation between two profiles.
     * @param profileIdFrom id of profile who wants to delete friendship.
     * @param profileIdTo if of friend profile.
     */
    void revokeFriendship(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException;
    
    /**
     * Keep only a certain number of recent friendshipEvents on profile.
     * @param id profile id.
     * @param numberOfEventsToKeep number of recent friendshipEvents to keep.
     */
    void keepEvents(String id, int numberOfEventsToKeep) throws ProfileNotFoundException;
    
    /**
     * Update the state of a profile.
     * @param id profile id.
     * @param state state.
     */
    void setState(String id, ProfileStateEnum state) throws ProfileNotFoundException;
    
    /**
     * Get skill of a profile.
     * @param id profile id.
     * @return skill, or 0L if profile doesn't exist.
     */
    long getSkill(String id);
    
    /**
     * Sum skill of a team's profiles.
     * @param ids profile ids.
     * @return skill sum (unknown profiles having a skill to 0L).
     */
    long sumSkill(Collection<String> ids);
}
