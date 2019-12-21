package manon.err;

/**
 * Business exceptions should be <i>unchecked exceptions</i> to make code more readable and flexible; they also rollback database transactions.<br>
 * Ideally, and for better performance, methods should return error code instead of throwing exceptions.<br>
 * TODO minimize usage of exceptions with error codes.
 * <br><br>
 * To understand why default (currently used here) Spring configuration makes these exceptions to rollback transactions, see
 * <a href="https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/data-access.html#transaction-declarative-rolling-back">Spring 5.1.7 documentation
 * - <i>Rolling Back a Declarative Transaction</i></a> (also, link to the
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction-declarative-rolling-back"><i>latest</i> version</a>).
 * TL;DR:<cite>
 * In its default configuration, the Spring Framework’s transaction infrastructure code
 * marks a transaction for rollback only in the case of runtime, unchecked exceptions.
 * That is, when the thrown exception is an instance or subclass of <code>RuntimeException</code>. (
 * <code>Error</code> instances also, by default, result in a rollback). Checked exceptions that are
 * thrown from a transactional method do not result in rollback in the default
 * configuration.</cite>
 */
@SuppressWarnings("serial")
public abstract class AbstractManagedException extends RuntimeException {

    public String getErrorType() {
        return this.getClass().getSimpleName();
    }
}
