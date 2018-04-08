package manon.app.trace.document;

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
        return new Object[][]{
                {AppTrace.builder().build(), AppTrace.builder().build(), true},
                {AppTrace.builder().creationDate(Tools.now()).build(), AppTrace.builder().build(), true},
                {AppTrace.builder().creationDate(Tools.now()).build(), AppTrace.builder().creationDate(Tools.yesterday()).build(), true},
                {AppTrace.builder().creationDate(Tools.now()).build(), AppTrace.builder().creationDate(Tools.now()).build(), true},
                {AppTrace.builder().id("1").build(), AppTrace.builder().build(), false}
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
