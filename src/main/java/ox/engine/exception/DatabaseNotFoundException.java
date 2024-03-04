package ox.engine.exception;

public class DatabaseNotFoundException extends OxRuntimeException {
    public DatabaseNotFoundException() {
        super("database.notfound.exception");
    }

    public DatabaseNotFoundException(String message) {
        super(message);
    }
}
