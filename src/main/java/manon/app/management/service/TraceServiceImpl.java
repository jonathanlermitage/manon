package manon.app.management.service;

import lombok.extern.slf4j.Slf4j;
import manon.app.management.document.AppTrace;
import manon.app.management.repository.AppTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static manon.util.Tools.str;

/**
 * Log messages to database.
 */
@Service
@Slf4j
public class TraceServiceImpl implements TraceService {
    
    private final AppTraceRepository appTraceRepository;
    
    @Autowired
    public TraceServiceImpl(AppTraceRepository appTraceRepository) {
        this.appTraceRepository = appTraceRepository;
    }
    
    @Override
    public void debug(String msg, Object... args) {
        log(AppTrace.TRACE_LEVEL.DEBUG, msg, args);
    }
    
    @Override
    public void error(String msg, Object... args) {
        log(AppTrace.TRACE_LEVEL.ERROR, msg, args);
    }
    
    @Override
    public void info(String msg, Object... args) {
        log(AppTrace.TRACE_LEVEL.INFO, msg, args);
    }
    
    @Override
    public void warn(String msg, Object... args) {
        log(AppTrace.TRACE_LEVEL.WARNING, msg, args);
    }
    
    @Override
    public void log(AppTrace.TRACE_LEVEL level, String msg, Object... args) {
        appTraceRepository.save(AppTrace.builder().msg(str(msg, args)).level(level).build());
    }
    
    @Override
    public void log(AppTrace.TRACE_LEVEL level, AppTrace.CAT category, String msg, Object... args) {
        appTraceRepository.save(AppTrace.builder().msg(str(msg, args)).cat(category).level(level).build());
    }
}
