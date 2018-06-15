package manon.app.trace.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.trace.document.AppTrace;
import manon.app.trace.model.AppTraceEvent;
import manon.app.trace.model.AppTraceLevel;
import manon.app.trace.repository.AppTraceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static manon.app.trace.model.AppTraceEvent.UPTIME;
import static manon.app.trace.model.AppTraceLevel.DEBUG;
import static manon.util.Tools.objId;

/**
 * Log messages to database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppTraceServiceImpl implements AppTraceService {
    
    private final AppTraceRepository appTraceRepository;
    
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date startupDate = new Date();
    
    @Getter
    private final String appId = objId();
    
    @Override
    public Mono<Void> deleteByCurrentAppIdAndEvent(AppTraceEvent event) {
        return appTraceRepository.deleteByAppIdAndEvent(appId, event).then();
    }
    
    @Override
    public Mono<Void> log(AppTraceLevel level, AppTraceEvent event, String msg) {
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
        return appTraceRepository.save(AppTrace.builder()
                .appId(appId)
                .msg(msg)
                .event(event)
                .level(level)
                .build()).then();
    }
    
    @Override
    public Mono<Void> log(AppTraceLevel level, AppTraceEvent event) {
        return log(level, event, null);
    }
    
    @Override
    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)
    public void logUptime() {
        deleteByCurrentAppIdAndEvent(UPTIME)
                .then(log(DEBUG, UPTIME, format("Application [%s] is alive since %ss (%s)",
                        appId,
                        (currentTimeMillis() - startupDate.getTime()) / 1_000,
                        dateFormat.format(startupDate))
                )).block();
    }
    
    // VisibleForTesting
    
    @Override
    public long count() {
        return appTraceRepository.count().block();
    }
    
    @Override
    public long countByCurrentAppId() {
        return appTraceRepository.countByAppId(appId).block();
    }
    
    @Override
    public long countByCurrentAppIdAndEvent(AppTraceEvent event) {
        return appTraceRepository.countByAppIdAndEvent(appId, event).block();
    }
    
    @Override
    public List<AppTrace> findAll() {
        return appTraceRepository.findAll().collectList().block();
    }
    
    // end of VisibleForTesting
}
