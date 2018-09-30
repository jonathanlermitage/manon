package manon.app.trace.document;

import manon.app.trace.model.AppTraceEvent;
import manon.app.trace.model.AppTraceLevel;
import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class AppTraceTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(AppTrace.builder().build().toString()).contains(
            "id", "appId",
            "msg", "level", "event",
            "creationDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        AppTrace filled = AppTrace.builder()
            .id("1")
            .appId("2")
            .msg("m")
            .level(AppTraceLevel.INFO)
            .event(AppTraceEvent.APP_START)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {AppTrace.builder().build(), AppTrace.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id("99").build(), filled, false},
            {filled.toBuilder().appId("99").build(), filled, false},
            {filled.toBuilder().msg("updated").build(), filled, false},
            {filled.toBuilder().level(AppTraceLevel.WARN).build(), filled, false},
            {filled.toBuilder().event(AppTraceEvent.UPTIME).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(AppTrace o1, AppTrace o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(AppTrace o1, AppTrace o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
