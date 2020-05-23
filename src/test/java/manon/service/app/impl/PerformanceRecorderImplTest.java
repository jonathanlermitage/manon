package manon.service.app.impl;

import manon.util.Tools;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceRecorderImplTest {

    private static final String STATS_TITLE = "Services performance (ms):";
    private static final String STATS_HEADER = " calls     min     max     total     avg  median  name";

    @Test
    void shouldEmptyShowStats() {
        PerformanceRecorderImpl performanceRecorder = new PerformanceRecorderImpl(Tools.CLOCK);
        assertThat(performanceRecorder.getStats()).isEmpty();
    }

    @Test
    void shouldShowStats() {
        PerformanceRecorderImpl performanceRecorder = new PerformanceRecorderImpl(Tools.CLOCK);

        performanceRecorder.saveTime("manon.user.UserService:do1()", 1);
        performanceRecorder.saveTime("manon.user.UserService:do1()", 2);
        performanceRecorder.saveTime("manon.user.UserService:do1()", 2);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 100);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 200);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 300);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 200);

        assertThat(performanceRecorder.getStats()).isEqualTo(STATS_TITLE + "\n" + STATS_HEADER + "\n" +
            "     3       1       2         5       1       2  m.user.UserService:do1()\n" +
            "     4     100     300       800     200     200  m.user.UserService:do2()\n" +
            STATS_HEADER);
    }

    @Test
    void shouldShowStatsManyTimesWithDifferentOrders() {
        PerformanceRecorderImpl performanceRecorder = new PerformanceRecorderImpl(Tools.CLOCK);

        performanceRecorder.saveTime("manon.user.UserService:do1()", 1);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 100);

        assertThat(performanceRecorder.getStats()).isEqualTo(STATS_TITLE + "\n" + STATS_HEADER + "\n" +
            "     1       1       1         1       1       1  m.user.UserService:do1()\n" +
            "     1     100     100       100     100     100  m.user.UserService:do2()\n" +
            STATS_HEADER);

        performanceRecorder.saveTime("manon.user.UserService:do1()", 2);
        performanceRecorder.saveTime("manon.user.UserService:do1()", 2000);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 200);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 300);
        performanceRecorder.saveTime("manon.user.UserService:do2()", 200);

        assertThat(performanceRecorder.getStats()).isEqualTo(STATS_TITLE + "\n" + STATS_HEADER + "\n" +
            "     4     100     300       800     200     200  m.user.UserService:do2()\n" +
            "     3       1    2000      2003     667       2  m.user.UserService:do1()\n" +
            STATS_HEADER);
    }
}
