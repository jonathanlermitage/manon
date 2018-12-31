package manon.user.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSnapshotTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserSnapshot.builder().build().toString()).contains(
            "id", "user", "creationDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserSnapshot filled = UserSnapshot.builder()
            .id("1")
            .user(User.builder().id("2").build())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserStats.builder().build(), UserStats.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id("99").build(), filled, false},
            {filled.toBuilder().user(User.builder().id("99").build()).build(), filled, false},
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
