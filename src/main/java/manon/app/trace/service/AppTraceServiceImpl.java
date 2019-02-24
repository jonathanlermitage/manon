package manon.app.trace.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.trace.document.AppTrace;
import manon.app.trace.model.AppTraceEvent;
import manon.app.trace.model.AppTraceLevel;
import manon.app.trace.repository.AppTraceRepository;
import manon.util.Tools;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static manon.app.trace.model.AppTraceEvent.UPTIME;
import static manon.app.trace.model.AppTraceLevel.DEBUG;

/**
 * Log messages to database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppTraceServiceImpl implements AppTraceService {
    
    private final AppTraceRepository appTraceRepository;
    private final Clock clock;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime startupDate = Tools.now();
    
    @Getter
    private final String appId = Long.toString(currentTimeMillis());
    
    @Override
    public void deleteByCurrentAppIdAndEvent(AppTraceEvent event) {
        appTraceRepository.deleteByAppIdAndEvent(appId, event);
    }
    
    @Override
    public void log(AppTraceLevel level, AppTraceEvent event, String msg) {
        String logmsg = "[" + event.name() + "]" + (msg == null ? "" : " " + msg);
        switch (level) {
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
            .appId(appId)
            .msg(msg)
            .event(event)
            .level(level)
            .build());
    }
    
    @Override
    public void log(AppTraceLevel level, AppTraceEvent event) {
        log(level, event, null);
    }
    
    @Override
    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)
    public void logUptime() {
        deleteByCurrentAppIdAndEvent(UPTIME);
        log(DEBUG, UPTIME, format("Application [%s] is alive since %s (%s)",
            appId,
            Duration.between(startupDate, Tools.now()).toString(),
            dateFormat.format(startupDate))
        );
    }
    
    // VisibleForTesting
    
    @Override
    public long count() {
        return appTraceRepository.count();
    }
    
    @Override
    public long countByCurrentAppId() {
        return appTraceRepository.countByAppId(appId);
    }
    
    @Override
    public long countByCurrentAppIdAndEvent(AppTraceEvent event) {
        return appTraceRepository.countByAppIdAndEvent(appId, event);
    }
    
    @Override
    public List<AppTrace> findAll() {
        return appTraceRepository.findAll();
    }
    
    // end of VisibleForTesting
}
