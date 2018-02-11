package manon.app.management.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.management.document.AppTrace;
import manon.app.management.repository.AppTraceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static manon.app.management.document.AppTrace.Event.UPTIME;
import static manon.app.management.document.AppTrace.Level.DEBUG;
import static manon.util.Tools.objId;

/**
 * Log messages to database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppTraceServiceImpl implements AppTraceService {
    
    private final AppTraceRepository appTraceRepository;
    
    private final DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
    private Date startupDate = new Date();
    
    @Getter
    private final String appId = objId();
    
    @Override
    public void deleteByCurrentAppIdAndEvent(AppTrace.Event event) {
        appTraceRepository.deleteByAppIdAndEvent(appId, event);
    }
    
    @Override
    public void log(AppTrace.Level level, AppTrace.Event event, String msg, Object... args) {
        String formattedMsg = msg == null ? null : args == null || args.length == 0 ? msg : format(msg, args);
        String logmsg = "[" + event.name() + "]" + (formattedMsg == null ? "" : " " + formattedMsg);
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
                .msg(formattedMsg)
                .event(event)
                .level(level)
                .build());
    }
    
    @Override
    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)
    public void logUptime() {
        deleteByCurrentAppIdAndEvent(UPTIME);
        log(DEBUG, UPTIME, "Application [%s] is alive since %ss (%s)",
                appId,
                (currentTimeMillis() - startupDate.getTime()) / 1_000,
                dateFormat.format(startupDate));
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
    public long countByCurrentAppIdAndEvent(AppTrace.Event event) {
        return appTraceRepository.countByAppIdAndEvent(appId, event);
    }
    
    @Override
    public List<AppTrace> findAll() {
        return appTraceRepository.findAll();
    }
    
    // end of VisibleForTesting
}
