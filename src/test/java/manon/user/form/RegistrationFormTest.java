package manon.user.form;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationFormTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(RegistrationForm.builder().build().toString()).contains(
            "username", "password");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        RegistrationForm filled = RegistrationForm.builder()
            .username("u")
            .password("p")
            .build();
        return new Object[][]{
            {RegistrationForm.builder().build(), RegistrationForm.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().password("updated").build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(RegistrationForm o1, RegistrationForm o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(RegistrationForm o1, RegistrationForm o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
