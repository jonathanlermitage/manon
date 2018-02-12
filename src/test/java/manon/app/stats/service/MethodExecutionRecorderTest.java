package manon.app.stats.service;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MethodExecutionRecorderTest {
    
    private static final String STATS_HEADER = "Services performance (ms):\n" +
            " calls     min     max     total     avg  median  name";
    
    @Test
    public void shouldEmptyShowStats() {
        MethodExecutionRecorder methodExecutionRecorder = new MethodExecutionRecorder();
        assertTrue(methodExecutionRecorder.isEmpty());
        assertEquals(methodExecutionRecorder.showStats(), "");
    }
    
    @Test
    public void shouldShowStats() {
        MethodExecutionRecorder methodExecutionRecorder = new MethodExecutionRecorder();
        
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 1);
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 2);
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 2);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 100);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 200);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 300);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 200);
        
        assertFalse(methodExecutionRecorder.isEmpty());
        assertEquals(methodExecutionRecorder.showStats(), STATS_HEADER + "\n" +
                "     3       1       2         5       1       2  m.user.UserService:do1()\n" +
                "     4     100     300       800     200     200  m.user.UserService:do2()");
    }
    
    @Test
    public void shouldShowStatsManyTimesWithDifferentOrders() {
        MethodExecutionRecorder methodExecutionRecorder = new MethodExecutionRecorder();
        
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 1);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 100);
    
        assertFalse(methodExecutionRecorder.isEmpty());
        assertEquals(methodExecutionRecorder.showStats(), STATS_HEADER + "\n" +
                "     1       1       1         1       1       1  m.user.UserService:do1()\n" +
                "     1     100     100       100     100     100  m.user.UserService:do2()");
        
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 2);
        methodExecutionRecorder.saveTime("manon.user.UserService:do1()", 2000);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 200);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 300);
        methodExecutionRecorder.saveTime("manon.user.UserService:do2()", 200);
    
        assertFalse(methodExecutionRecorder.isEmpty());
        assertEquals(methodExecutionRecorder.showStats(), STATS_HEADER + "\n" +
                "     4     100     300       800     200     200  m.user.UserService:do2()\n" +
                "     3       1    2000      2003     667       2  m.user.UserService:do1()");
    }
}
