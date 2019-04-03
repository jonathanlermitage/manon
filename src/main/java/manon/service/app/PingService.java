package manon.service.app;

import manon.err.app.PingException;

import java.io.UnsupportedEncodingException;

public interface PingService {
    
    void ping(String encodedUrl) throws PingException, UnsupportedEncodingException;
}
