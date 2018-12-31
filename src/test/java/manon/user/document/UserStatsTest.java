package manon.user.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatsTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserStats.builder().build().toString()).contains(
            "id", "nbUsers", "creationDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserStats filled = UserStats.builder()
            .id("1")
            .nbUsers(2)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserStats.builder().build(), UserStats.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id("99").build(), filled, false},
            {filled.toBuilder().nbUsers(99).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(Object o1, Object o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Object o1, Object o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
