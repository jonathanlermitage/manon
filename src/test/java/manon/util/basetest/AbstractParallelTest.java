package manon.util.basetest;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Used to invoke test methods in parallel.
 * See {@code junit-platform.properties} for parallelism configuration.
 */
@Execution(value = ExecutionMode.CONCURRENT)
public abstract class AbstractParallelTest {
}
