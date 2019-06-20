package manon.service.app.impl;

import manon.util.Tools;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/** Test cache applied on {@link manon.service.app.impl.AuthTokenServiceImpl}. */
public class AuthTokenServiceIT extends AbstractIT {
    
    @Test
    public void shouldUseCacheOnExistsMethod() {
        long tokenId1 = authTokenService.create(name(1), Tools.tomorrow()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        Mockito.verify(authTokenService, Mockito.times(0)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(0)).exists(tokenId2);
        
        for (int i = 0; i < 3; i++) {
            assertThat(authTokenService.exists(tokenId1)).isTrue();
        }
        for (int i = 0; i < 6; i++) {
            assertThat(authTokenService.exists(tokenId2)).isTrue();
        }
        
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
    }
    
    @Test
    public void shouldClearCacheWhenUserRevokesHisTokens() {
        long tokenId1 = authTokenService.create(name(1), Tools.tomorrow()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        assertThat(authTokenService.exists(tokenId1)).isTrue();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
        
        authTokenService.removeUserTokens(name(1));
        assertThat(authTokenService.exists(tokenId1)).isFalse();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
    }
    
    @Test
    public void shouldClearCacheWhenRevokeAllExpiredTokens() {
        long tokenId1 = authTokenService.create(name(1), Tools.yesterday()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        assertThat(authTokenService.exists(tokenId1)).isTrue();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
        
        authTokenService.removeAllExpired();
        assertThat(authTokenService.exists(tokenId1)).isFalse();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
    }
    
    @Test
    public void shouldClearCacheWhenDeleteAllTokens() {
        long tokenId1 = authTokenService.create(name(1), Tools.tomorrow()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        assertThat(authTokenService.exists(tokenId1)).isTrue();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
        
        authTokenService.deleteAll();
        assertThat(authTokenService.exists(tokenId1)).isFalse();
        assertThat(authTokenService.exists(tokenId2)).isFalse();
        
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId2);
    }
    
    @Test
    public void shouldClearCacheWhenEvictEntireTokensCache() {
        long tokenId1 = authTokenService.create(name(1), Tools.tomorrow()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        assertThat(authTokenService.exists(tokenId1)).isTrue();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
        
        authTokenService.evictAllCache();
        assertThat(authTokenService.exists(tokenId1)).isTrue();
        assertThat(authTokenService.exists(tokenId2)).isTrue();
        
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId2);
    }
    
    @Test
    public void shouldRebuildCacheOnExistsMethodAfterCacheEviction() {
        long tokenId1 = authTokenService.create(name(1), Tools.tomorrow()).getId();
        long tokenId2 = authTokenService.create(name(2), Tools.tomorrow()).getId();
        Mockito.verify(authTokenService, Mockito.times(0)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(0)).exists(tokenId2);
    
        for (int i = 0; i < 3; i++) {
            assertThat(authTokenService.exists(tokenId1)).isTrue();
        }
        for (int i = 0; i < 6; i++) {
            assertThat(authTokenService.exists(tokenId2)).isTrue();
        }
    
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(1)).exists(tokenId2);
        
        authTokenService.evictAllCache();
    
        for (int i = 0; i < 3; i++) {
            assertThat(authTokenService.exists(tokenId1)).isTrue();
        }
        for (int i = 0; i < 6; i++) {
            assertThat(authTokenService.exists(tokenId2)).isTrue();
        }
    
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId1);
        Mockito.verify(authTokenService, Mockito.times(2)).exists(tokenId2);
    }
}
