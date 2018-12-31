package manon.app.trace.service;

import manon.app.trace.model.AppTraceLevel;
import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static manon.app.trace.model.AppTraceEvent.APP_START;
import static manon.app.trace.model.AppTraceEvent.UPTIME;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTraceServiceTest extends AbstractInitBeforeClass {
    
    @Autowired
    private AppTraceService appTraceService;
    
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
    
    @DataProvider
    public static Object[] dataProviderShouldLog() {
        return AppTraceLevel.values();
    }
    
    @Test(dataProvider = "dataProviderShouldLog")
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
