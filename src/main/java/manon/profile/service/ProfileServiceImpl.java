package manon.profile.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import manon.profile.ProfileNotFoundException;
import manon.profile.ProfileStateEnum;
import manon.profile.ProfileUpdateForm;
import manon.profile.document.Profile;
import manon.profile.friendship.FriendshipExistsException;
import manon.profile.friendship.FriendshipRequestExistsException;
import manon.profile.friendship.FriendshipRequestNotFoundException;
import manon.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static manon.profile.document.Profile.Validation.MAX_EVENTS;

@Service("ProfileService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProfileServiceImpl implements ProfileService {
    
    private static final String CACHE_READ_PROFILE_BY_ID = "READ_PROFILE_BY_ID";
    private final ProfileRepository profileRepository;
    
    @Override
    public long count() {
        return profileRepository.count();
    }
    
    @Caching(put = @CachePut(value = CACHE_READ_PROFILE_BY_ID, key = "#result.id"))
    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }
    
    @Override
    public void ensureExist(String... ids) throws ProfileNotFoundException {
        for (String id : ids) {
            readOne(id);
        }
    }
    
    @Cacheable(value = CACHE_READ_PROFILE_BY_ID, key = "#id")
    @Override
    public Profile readOne(String id) throws ProfileNotFoundException {
        return profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
    }
    
    @CachePut(value = CACHE_READ_PROFILE_BY_ID, key = "#result.id")
    @Override
    public Profile create() {
        Profile profile = Profile.builder().build();
        profileRepository.save(profile);
        return profile;
    }
    
    @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileId")
    @Override
    public void update(String profileId, ProfileUpdateForm profileUpdateForm)
            throws ProfileNotFoundException, DuplicateKeyException {
        profileRepository.updateField(profileId, profileUpdateForm.getField(), profileUpdateForm.getValue());
    }
    
    @Caching(evict = {
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdFrom"),
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdTo")
    })
    @Override
    public void askFriendship(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException, FriendshipExistsException, FriendshipRequestExistsException {
        Profile from = readOne(profileIdFrom);
        if (from.getFriends().contains(profileIdTo)) {
            throw new FriendshipExistsException(profileIdFrom, profileIdTo);
        }
        if (from.getFriendshipRequestsFrom().contains(profileIdTo)) {
            throw new FriendshipRequestExistsException(profileIdTo, profileIdFrom);
        }
        if (from.getFriendshipRequestsTo().contains(profileIdTo)) {
            throw new FriendshipRequestExistsException(profileIdFrom, profileIdTo);
        }
        profileRepository.askFriendship(profileIdFrom, profileIdTo);
        keepEvents(profileIdFrom);
        keepEvents(profileIdTo);
    }
    
    @Caching(evict = {
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdFrom"),
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdTo")
    })
    @Override
    public void acceptFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException {
        if (!readOne(profileIdTo).getFriendshipRequestsFrom().contains(profileIdFrom)) {
            throw new FriendshipRequestNotFoundException(profileIdFrom, profileIdTo);
        }
        profileRepository.acceptFriendshipRequest(profileIdFrom, profileIdTo);
        keepEvents(profileIdFrom);
        keepEvents(profileIdTo);
    }
    
    @Caching(evict = {
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdFrom"),
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdTo")
    })
    @Override
    public void rejectFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException {
        profileRepository.rejectFriendshipRequest(profileIdFrom, profileIdTo);
        keepEvents(profileIdFrom);
        keepEvents(profileIdTo);
    }
    
    @Caching(evict = {
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdFrom"),
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdTo")
    })
    @Override
    public void cancelFriendshipRequest(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException {
        profileRepository.cancelFriendshipRequest(profileIdFrom, profileIdTo);
        keepEvents(profileIdFrom);
        keepEvents(profileIdTo);
    }
    
    @Caching(evict = {
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdFrom"),
            @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#profileIdTo")
    })
    @Override
    public void revokeFriendship(String profileIdFrom, String profileIdTo)
            throws ProfileNotFoundException {
        profileRepository.revokeFriendship(profileIdFrom, profileIdTo);
        keepEvents(profileIdFrom);
        keepEvents(profileIdTo);
    }
    
    @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#id")
    @Override
    public void keepEvents(String id) throws ProfileNotFoundException {
        profileRepository.keepEvents(id, MAX_EVENTS);
    }
    
    @CacheEvict(value = CACHE_READ_PROFILE_BY_ID, key = "#id")
    @Override
    public void setState(String id, ProfileStateEnum state)
            throws ProfileNotFoundException {
        profileRepository.setState(id, state);
    }
    
    @Override
    public long getSkill(String id) {
        return profileRepository.getSkill(id);
    }
    
    @Override
    public long sumSkill(Collection<String> ids) {
        return profileRepository.sumSkill(ids);
    }
}
