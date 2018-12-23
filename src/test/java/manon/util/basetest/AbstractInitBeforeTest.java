package manon.util.basetest;

import org.testng.annotations.BeforeMethod;

/**
 * Recreate data before every test method.
 */
public abstract class AbstractInitBeforeTest extends AbstractInitBeforeClass {
    
    /** Clear data before test methods. Do NOT override it in non-abstract test classes. */
    @Override
    @BeforeMethod
    public void beforeMethod() throws Exception {
        setInitialized(false);
        super.beforeMethod();
    }
}
