package manon.service.app;

import manon.util.ExistForTesting;

public interface TrxDemoService {

    /**
     * Create a sample random user, then fail by throwing given error.
     * @param throwableClass {@code Exception.class}, {@code Error.class} or {@code RuntimeException.class}, otherwise don't fail.
     */
    @ExistForTesting
    void createSampleUserThenFail(Class<? extends Throwable> throwableClass) throws Error, Exception;
}
