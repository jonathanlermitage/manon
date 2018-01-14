package manon.app.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.management.document.AppTrace;
import manon.app.management.repository.AppTraceRepository;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Log messages to database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TraceServiceImpl implements TraceService {
    
    private final AppTraceRepository appTraceRepository;
    
    @Override
    public void log(AppTrace.LVL level, AppTrace.CAT category, String msg, Object... args) {
        String logmsg = "[" + category.name() + "] " + msg;
        switch (level) {
            case DEBUG:
                log.debug(logmsg);
                break;
            case INFO:
                log.info(logmsg);
                break;
            case WARN:
                log.warn(logmsg);
                break;
            case ERROR:
                log.error(logmsg);
                break;
        }
        appTraceRepository.save(AppTrace.builder()
                .msg(format(msg, args))
                .cat(category)
                .level(level)
                .build());
    }
}
