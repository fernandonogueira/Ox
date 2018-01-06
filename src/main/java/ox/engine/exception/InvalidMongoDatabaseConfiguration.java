package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/15/14 9:12 AM
 */
public class InvalidMongoDatabaseConfiguration extends GenericMongoMigrationRuntimeException {
    public InvalidMongoDatabaseConfiguration(String msg) {
        super(msg);
    }
}