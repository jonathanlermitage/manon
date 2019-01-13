package manon.util.basetest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

/**
 * Recreate data before every test method.
 */
@Slf4j
public abstract class AbstractInitBeforeTest extends AbstractInitBeforeClass {
    
    /** Clear data before test methods. Do NOT override it in non-abstract test classes. */
    @Override
    @BeforeEach
    public void beforeMethod() throws Exception {
        setInitialized(false);
        super.beforeMethod();
    }
}
