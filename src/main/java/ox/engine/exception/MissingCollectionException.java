package ox.engine.exception;

public class MissingCollectionException extends OxRuntimeException {
    public MissingCollectionException() {
        super("collection.not.found");
    }

    public MissingCollectionException(String msg) {
        super(msg);
    }
}
