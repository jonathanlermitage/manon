package manon.app.trace.service;

import manon.app.trace.model.AppTraceLevel;
import manon.util.basetest.InitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static manon.app.trace.model.AppTraceEvent.APP_START;
import static manon.app.trace.model.AppTraceEvent.UPTIME;
import static org.testng.Assert.assertEquals;

public class AppTraceServiceTest extends InitBeforeClass {
    
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
        assertEquals(appTraceService.count(), nbTraces + 1);
        assertEquals(appTraceService.countByCurrentAppId(), appTraceService.count());
        assertEquals(appTraceService.countByCurrentAppIdAndEvent(UPTIME), 1);
        
        String appId = appTraceService.getAppId();
        appTraceService.findAll().forEach(appTrace -> assertEquals(appTrace.getAppId(), appId));
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
        assertEquals(appTraceService.count(), nbTraces + 6);
        assertEquals(appTraceService.countByCurrentAppId(), appTraceService.count());
        assertEquals(appTraceService.countByCurrentAppIdAndEvent(APP_START), 6);
        
        String appId = appTraceService.getAppId();
        appTraceService.findAll().forEach(appTrace -> assertEquals(appTrace.getAppId(), appId));
    }
}
