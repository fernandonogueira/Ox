package ox.engine.exception;

/**
 * @author Fernando Nogueira
 * @since 4/15/14 1:36 AM
 */
public class InvalidMongoConfiguration extends GenericMongoMigratorException {
    public InvalidMongoConfiguration(String s) {
        super(s);
    }
}
