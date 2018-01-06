package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/15/14 8:49 AM
 */
public class GenericMongoMigrationRuntimeException extends RuntimeException {
    public GenericMongoMigrationRuntimeException(String msg) {
        super(msg);
    }

    public GenericMongoMigrationRuntimeException(String msg, Exception e) {
        super(msg, e);
    }
}
