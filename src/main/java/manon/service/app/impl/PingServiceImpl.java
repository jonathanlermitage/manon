package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.err.app.PingException;
import manon.service.app.PingService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PingServiceImpl implements PingService {
    
    private final RestTemplate restTemplate;
    
    @Override
    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 50), include = PingException.class)
    public void ping(String encodedUrl) throws PingException, UnsupportedEncodingException {
        String url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.name());
        try {
            restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            log.debug("ping failed due to exception: " + e.getMessage());
            throw new PingException();
        }
    }
}
