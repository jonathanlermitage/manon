package manon.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import manon.app.config.Profiles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static manon.util.Tools.str;

@Aspect
@Component
@Slf4j
@Profile(Profiles.METRICS)
public class MethodExecutionTimeRecorder {
    
    private static final Map<String, ServiceStats> allStats = new HashMap<>();
    private static final Map<String, ServiceStats> hourlyStats = new HashMap<>();
    
    /**
     * Collect execution time on every WS class methods.
     */
    @Around("execution(* manon..*WS.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long execTime = System.currentTimeMillis() - start;
        String signature = point.getThis().getClass().getName();
        if (signature.contains("$")) {
            signature = signature.substring(0, signature.indexOf("$"));
        }
        Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        signature += ":" + method.getName() + Arrays.toString(Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray());
        saveTime(allStats, signature, execTime);
        saveTime(hourlyStats, signature, execTime);
        return result;
    }
    
    /**
     * Log and return all collected statistics (since application start).
     * Also, this method is execute hourly.
     * @return statistics as readable text.
     */
    @Scheduled(fixedRate = 3600_000, initialDelay = 3600_000)
    public static String showAllStats() {
        return showStats(allStats, "All stats");
    }
    
    /**
     * Log, return hourly collected statistics and reset them.
     * Also, this method is execute hourly.
     * @return statistics as readable text.
     */
    @Scheduled(fixedRate = 3600_000, initialDelay = 3600_000)
    public static String showHourlyStats() {
        String text = showStats(hourlyStats, "Last hour stats");
        hourlyStats.clear();
        return text;
    }
    
    /**
     * Log and return given statistics.
     * @param stats statistics.
     * @param title a title to append to readable text.
     * @return statistics as readable text.
     */
    @Synchronized
    private static String showStats(Map<String, ServiceStats> stats, String title) {
        List<ServiceStats> view = new ArrayList<>();
        view.addAll(stats.values());
        view.sort((o1, o2) -> o1.getTotalTime() > o2.getTotalTime() ? 1 : -1);
        StringBuilder buff = new StringBuilder(2048);
        buff.append("    calls       min       max       total       avg   name\n");
        for (ServiceStats s : view) {
            buff.append(str("%9s %9s %9s   %9s %9s   %s\n",
                    s.getCalls(),
                    s.getMinTime(),
                    s.getMaxTime(),
                    s.getTotalTime(),
                    s.getTotalTime() / s.getCalls(),
                    s.getService().replaceAll("\\[", "(").replaceAll("]", ")")));
        }
        String lines = buff.toString();
        log.info("\n" + title + " - services performance (ms): \n{}", lines);
        return lines;
    }
    
    /**
     * Collect statistics.
     * @param signature class and method signature.
     * @param execTime execution time.
     */
    @Synchronized
    public void saveTime(Map<String, ServiceStats> stats, String signature, long execTime) {
        ServiceStats stat;
        if (stats.containsKey(signature)) {
            stat = stats.get(signature);
            stat.setCalls(stat.getCalls() + 1L);
            if (execTime < stat.getMinTime()) {
                stat.setMinTime(execTime);
            }
            if (execTime > stat.getMaxTime()) {
                stat.setMaxTime(execTime);
            }
            stat.setTotalTime(execTime + stat.getTotalTime());
        } else {
            stat = new ServiceStats(signature, 1L, execTime, execTime, execTime);
        }
        stats.put(signature, stat);
    }
    
    /** Statistic. */
    @Data
    @AllArgsConstructor
    private final class ServiceStats {
        private String service;
        private long calls;
        private long minTime;
        private long maxTime;
        private long totalTime;
    }
}
