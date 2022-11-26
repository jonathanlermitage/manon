package manon.app.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import static manon.util.Tools.Mdc.KEY_CORRELATION_ID;
import static manon.util.Tools.Mdc.KEY_URI;
import static manon.util.Tools.Mdc.KEY_USER;
import static manon.util.Tools.isBlank;

@Configuration
@Slf4j
public class FilterConfig {

    @Bean
    @Primary
    public FilterRegistrationBean<LoggingFilter> getFilterRegistrationBean() {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    private static class LoggingFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            try {
                Thread.currentThread().setName(UUID.randomUUID().toString());
                Principal principal = null;
                if (request instanceof HttpServletRequest) {
                    principal = ((HttpServletRequest) request).getUserPrincipal();
                    MDC.put(KEY_URI, ((HttpServletRequest) request).getRequestURI());
                }
                MDC.put(KEY_USER, principal != null && !isBlank(principal.getName()) ? principal.getName() : "anonymous");
                MDC.put(KEY_CORRELATION_ID, Long.toString(System.nanoTime()));

                chain.doFilter(request, response);
            } catch (Error | Exception throwable) {
                if (!(throwable.getCause() instanceof BadCredentialsException) && !(throwable.getCause() instanceof DisabledException)) {
                    log.error("spring-mvc-error", throwable);
                }
                throw throwable;
            } finally {
                MDC.clear();
            }
        }

        @Override
        public void init(jakarta.servlet.FilterConfig arg) {
            // nothing to do
        }

        @Override
        public void destroy() {
            // nothing to do
        }
    }
}
