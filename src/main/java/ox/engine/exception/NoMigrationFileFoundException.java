package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/11/14 10:39 PM
 */
public class NoMigrationFileFoundException extends GenericMongoMigratorException {
    public NoMigrationFileFoundException(String s) {
        super(s);
    }
}
