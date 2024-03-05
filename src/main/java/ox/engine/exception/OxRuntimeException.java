package ox.engine.exception;

public class OxRuntimeException extends RuntimeException {
    public OxRuntimeException(String msg) {
        super(msg);
    }
    public OxRuntimeException(String msg, Exception e) {
        super(msg, e);
    }
}
