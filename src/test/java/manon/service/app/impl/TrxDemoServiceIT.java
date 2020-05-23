package manon.service.app.impl;

import manon.util.basetest.AbstractIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TrxDemoServiceIT extends AbstractIT {

    @Test
    void errorShouldRollbackTransaction() {
        //GIVEN some users in db
        long nbUsersBefore = userService.count();

        //WHEN create 1 user then throw an Error in the same transaction
        Assertions.assertThatThrownBy(() -> trxDemoService.createSampleUserThenFail(Error.class))
            .isInstanceOf(Error.class);

        //THEN Error should rollback transaction
        long nbUsersAfter = userService.count();
        Assertions.assertThat(nbUsersAfter).isEqualTo(nbUsersBefore);
    }

    @Test
    void runtimeExceptionShouldRollbackTransaction() {
        //GIVEN some users in db
        long nbUsersBefore = userService.count();

        //WHEN create 1 user then throw a RuntimeException in the same transaction
        Assertions.assertThatThrownBy(() -> trxDemoService.createSampleUserThenFail(RuntimeException.class))
            .isInstanceOf(RuntimeException.class);

        //THEN RuntimeException should rollback transaction
        long nbUsersAfter = userService.count();
        Assertions.assertThat(nbUsersAfter).isEqualTo(nbUsersBefore);
    }

    @Test
    void exceptionShouldNotRollbackTransaction() {
        //GIVEN some users in db
        long nbUsersBefore = userService.count();

        //WHEN create 1 user then throw an Exception in the same transaction
        Assertions.assertThatThrownBy(() -> trxDemoService.createSampleUserThenFail(Exception.class))
            .isInstanceOf(Exception.class);

        //THEN Exception should NOT rollback transaction
        long nbUsersAfter = userService.count();
        Assertions.assertThat(nbUsersAfter).isEqualTo(nbUsersBefore + 1);
    }
}
