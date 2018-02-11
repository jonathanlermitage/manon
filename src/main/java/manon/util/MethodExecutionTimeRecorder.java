package manon.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.Collections.sort;
import static manon.app.config.SpringProfiles.METRICS;

@Aspect
@Component
@Slf4j
@Profile(METRICS)
public class MethodExecutionTimeRecorder {
    
    private static final Map<String, ServiceStats> stats = new HashMap<>();
    
    /**
     * Collect execution time on every WS class methods.
     */
    @Around("execution(* manon..*WS.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = currentTimeMillis();
        Object result = point.proceed();
        long execTime = currentTimeMillis() - start;
        String signature = point.getThis().getClass().getName();
        if (signature.contains("$")) {
            signature = signature.substring(0, signature.indexOf("$"));
        }
        Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        signature += ":" + method.getName() + Arrays.toString(stream(method.getParameterTypes()).map(Class::getSimpleName).toArray());
        saveTime(stats, signature, execTime);
        return result;
    }
    
    /**
     * Log and return collected statistics since application start.
     * @return statistics as readable text.
     */
    public static String showStats() {
        return showStats(stats);
    }
    
    /**
     * Log and return given statistics.
     * @param stats statistics.
     * @return statistics as readable text.
     */
    @Synchronized
    private static String showStats(Map<String, ServiceStats> stats) {
        List<ServiceStats> view = new ArrayList<>(stats.values());
        view.sort((o1, o2) -> o1.getTotalTime() > o2.getTotalTime() ? 1 : -1);
        StringBuilder buff = new StringBuilder(2048);
        buff.append("\n calls     min     max     total     avg  median  name");
        view.stream().map(s -> format("\n%6s %7s %7s   %7s %7s %7s  %s",
                s.getCalls(),
                s.getMinTime(),
                s.getMaxTime(),
                s.getTotalTime(),
                s.getTotalTime() / s.getCalls(),
                ((long) median(s.getTimes())),
                "m." + s.getService().replaceAll("\\[", "(").replaceAll("]", ")").substring("manon.".length()))).forEach(buff::append);
        return "Services performance (ms):" + buff.toString();
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
            stat.getTimes().add(execTime);
            stat.setCalls(stat.getCalls() + 1L);
            if (execTime < stat.getMinTime()) {
                stat.setMinTime(execTime);
            }
            if (execTime > stat.getMaxTime()) {
                stat.setMaxTime(execTime);
            }
            stat.setTotalTime(execTime + stat.getTotalTime());
        } else {
            stat = new ServiceStats(signature, 1L, execTime, execTime, execTime, Stream.of(execTime).collect(Collectors.toList()));
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
        private List<Long> times;
    }
    
    private static double median(List<Long> v) {
        sort(v);
        int middle = v.size() / 2;
        if (v.size() % 2 == 1) {
            return v.get(middle);
        }
        return (v.get(middle - 1) + v.get(middle)) / 2.0;
    }
}
