package manon.user.form;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPasswordUpdateFormTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserPasswordUpdateForm.builder().build().toString()).contains(
            "oldPassword", "newPassword");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserPasswordUpdateForm filled = UserPasswordUpdateForm.builder()
            .oldPassword("o")
            .newPassword("n")
            .build();
        return new Object[][]{
            {UserPasswordUpdateForm.builder().build(), UserPasswordUpdateForm.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().oldPassword("updated").build(), filled, false},
            {filled.toBuilder().newPassword("updated").build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserPasswordUpdateForm o1, UserPasswordUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserPasswordUpdateForm o1, UserPasswordUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
