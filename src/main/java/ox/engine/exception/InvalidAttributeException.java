package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/15/14 8:34 AM
 */
public class InvalidAttributeException extends GenericMongoMigrationRuntimeException {
    public InvalidAttributeException(String s) {
        super(s);
    }
}
