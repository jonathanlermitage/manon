package manon.service.app.impl;

import manon.model.app.AppTraceLevel;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static manon.model.app.AppTraceEvent.APP_START;
import static manon.model.app.AppTraceEvent.UPTIME;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTraceServiceIT extends AbstractIT {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldLogUptime() {
        appTraceService.deleteByCurrentAppIdAndEvent(UPTIME);
        long nbTraces = appTraceService.count();
        for (int i = 0; i < 3; i++) {
            appTraceService.logUptime();
        }
        assertThat(appTraceService.count()).isEqualTo(nbTraces + 1);
        assertThat(appTraceService.countByCurrentAppId()).isEqualTo(appTraceService.count());
        assertThat(appTraceService.countByCurrentAppIdAndEvent(UPTIME)).isEqualTo(1);
        
        String appId = appTraceService.getAppId();
        appTraceService.findAll().forEach(appTrace -> assertThat(appTrace.getAppId()).isEqualTo(appId));
    }
    
    public Object[] dataProviderShouldLog() {
        return AppTraceLevel.values();
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldLog")
    public void shouldLog(AppTraceLevel level) {
        appTraceService.deleteByCurrentAppIdAndEvent(APP_START);
        long nbTraces = appTraceService.count();
        for (int i = 0; i < 3; i++) {
            appTraceService.log(level, APP_START, "foo");
            appTraceService.log(level, APP_START);
        }
        assertThat(appTraceService.count()).isEqualTo(nbTraces + 6);
        assertThat(appTraceService.countByCurrentAppId()).isEqualTo(appTraceService.count());
        assertThat(appTraceService.countByCurrentAppIdAndEvent(APP_START)).isEqualTo(6);
        
        String appId = appTraceService.getAppId();
        appTraceService.findAll().forEach(appTrace -> assertThat(appTrace.getAppId()).isEqualTo(appId));
    }
}
