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
    public void log(AppTrace.TRACE_LEVEL level, AppTrace.CAT category, String msg, Object... args) {
        appTraceRepository.save(AppTrace.builder().msg(str(msg, args)).cat(category).level(level).build());
    }
}
