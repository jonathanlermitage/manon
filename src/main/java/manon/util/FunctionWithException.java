package manon.util;

/**
 * Handle checked exceptions in stream.
 * See <a href="https://www.oreilly.com/ideas/handling-checked-exceptions-in-java-streams">online article</a>.
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> {
    
    R apply(T t) throws E;
}
