package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/17/14 2:19 PM
 */
public class CouldNotCreateCollectionException extends GenericMongoMigrationRuntimeException {
    public CouldNotCreateCollectionException(String s) {
        super(s);
    }
}
