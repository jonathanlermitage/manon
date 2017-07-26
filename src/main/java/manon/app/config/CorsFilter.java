package manon.app.config;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Enable Cross-Origin Resource Sharing (CORS).
 */
@Component
public class CorsFilter extends org.springframework.web.filter.CorsFilter {
    
    public CorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Accept, Origin, Content-Type, Depth, User-Agent, If-Modified-Since, Cache-Control, Authorization, X-Req, X-File-Size, X-Requested-With, X-File-Name");
        filterChain.doFilter(request, response);
    }
}
