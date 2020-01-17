package manon.util.basetest;

/**
 * Like {@link AbstractIT}, but application starts with no user and one admin.
 */
public abstract class AbstractNoUserIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 0;
    }
}
