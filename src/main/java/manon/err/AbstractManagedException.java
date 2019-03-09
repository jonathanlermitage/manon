package manon.err;

@SuppressWarnings("serial")
public abstract class AbstractManagedException extends Exception {
    
    public String getErrorType() {
        return this.getClass().getSimpleName();
    }
}
