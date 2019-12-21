package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import manon.service.app.TrxDemoService;
import manon.service.user.RegistrationService;
import manon.util.ExistForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrxDemoServiceImpl implements TrxDemoService {

    private final RegistrationService registrationService;

    @Override
    @ExistForTesting
    public void createSampleUserThenFail(Class<? extends Throwable> throwableClass) throws Error, Exception {
        createSampleUser();
        if (throwableClass.equals(Exception.class)) {
            throw new Exception();
        }
        if (throwableClass.equals(Error.class)) {
            throw new Error();
        }
        if (throwableClass.equals(RuntimeException.class)) {
            throw new RuntimeException();
        }
    }

    private void createSampleUser() {
        registrationService.registerPlayer("U" + System.nanoTime(), "password");
    }
}
