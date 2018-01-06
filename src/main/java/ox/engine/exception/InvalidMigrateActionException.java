package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/17/14 5:56 PM
 */
public class InvalidMigrateActionException extends GenericMongoMigratorException {
    public InvalidMigrateActionException(String s) {
        super(s);
    }
}
