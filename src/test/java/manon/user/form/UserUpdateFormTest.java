package manon.user.form;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserUpdateFormTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserUpdateForm.builder().build().toString()).contains(
            "nickname", "email");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserUpdateForm filled = UserUpdateForm.builder()
            .nickname("n")
            .email("e")
            .build();
        return new Object[][]{
            {UserUpdateForm.builder().build(), UserUpdateForm.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().nickname("updated").build(), filled, false},
            {filled.toBuilder().email("updated").build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserUpdateForm o1, UserUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserUpdateForm o1, UserUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
