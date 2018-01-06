package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/19/14 6:21 PM
 */
public class IndexAlreadyExistsException extends GenericMongoMigrationRuntimeException {
    public IndexAlreadyExistsException(String msg) {
        super(msg);
    }
}
