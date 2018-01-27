package manon.app.security;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

import static manon.util.Tools.MDC_KEY_REQUEST_ID;
import static manon.util.Tools.MDC_KEY_USER;
import static manon.util.Tools.isBlank;

public class LoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Principal principal = null;
            if (request instanceof HttpServletRequest) {
                principal = ((HttpServletRequest) request).getUserPrincipal();
                String requestId = ((HttpServletRequest) request).getHeader("X-Request-Id");
                if (requestId != null) {
                    MDC.put(MDC_KEY_REQUEST_ID, requestId);
                }
            }
            MDC.put(MDC_KEY_USER, principal != null && !isBlank(principal.getName()) ? principal.getName() : "anonymous");
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
    
    @Override
    public void init(FilterConfig arg) {
    }
    
    @Override
    public void destroy() {
    }
}
