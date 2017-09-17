package manon.profile.service;

import com.mongodb.DuplicateKeyException;
import manon.profile.ProfileFieldEnum;
import manon.profile.ProfileNotFoundException;
import manon.profile.ProfileStateEnum;
import manon.profile.ProfileUpdateForm;
import manon.profile.document.Profile;
import manon.profile.friendship.FriendshipExistsException;
import manon.profile.friendship.FriendshipRequestExistsException;
import manon.profile.friendship.FriendshipRequestNotFoundException;
import manon.profile.repository.ProfileRepository;

import java.util.Collection;

public interface ProfileService {
    
    long count();
    
    void save(Profile profile);
    
    void ensureExist(String... ids) throws ProfileNotFoundException;
    
    Profile readOne(String id) throws ProfileNotFoundException;
    
    /**
     * Create a fresh and blank profile.
     * @return created profile.
     */
    Profile create();
    
    /**
     * Update a profile's field.
     * @see ProfileRepository#updateField(String, ProfileFieldEnum, Object)
     * @param profileId profile id.
     * @param profileUpdateForm field name and its new value.
     */
    void update(String profileId, ProfileUpdateForm profileUpdateForm)
            throws ProfileNotFoundException, DuplicateKeyException;
    
    /**
     * Add a friendship request between two profiles.
     * @see ProfileRepository#askFriendship(String, String)
     * @param profileIdFrom id of profile that asks for friendship.
     * @param profileIdTo id of profile that is targeted.
     */
    void askFriendship(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException, FriendshipExistsException, FriendshipRequestExistsException;
    
    /**
     * Create a friendship request between two profiles.
     * @see ProfileRepository#acceptFriendshipRequest(String, String)
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that accepts friendship.
     */
    void acceptFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException;
    
    /**
     * Reject a friendship request between two profiles.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @see ProfileRepository#rejectFriendshipRequest(String, String)
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that rejects friendship.
     */
    void rejectFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException;
    
    /**
     * Cancel a friendship request between two profiles.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @see ProfileRepository#acceptFriendshipRequest(String, String)
     * @param profileIdFrom id of profile that asked for friendship.
     * @param profileIdTo id of profile that accepts friendship.
     */
    void cancelFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException;
    
    /**
     * Delete a friendship relation between two profiles.
     * Don't fail if friendship doesn't exist, it won't corrupt data.
     * @see ProfileRepository#revokeFriendship(String, String)
     * @param profileIdFrom id of profile who wants to delete friendship.
     * @param profileIdTo if of friend profile.
     */
    void revokeFriendship(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException;
    
    /**
     * Keep only {@link Profile.Validation#MAX_EVENTS} recent friendshipEvents on profile.
     * @param id profile id.
     */
    void keepEvents(String id) throws ProfileNotFoundException;
    
    /**
     * Update the state of a profile.
     * @param id profile id.
     * @param state state.
     */
    void setState(String id, ProfileStateEnum state)
            throws ProfileNotFoundException;
    
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
