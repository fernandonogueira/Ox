package ox.engine.exception;

public class InvalidMongoDatabaseConfiguration extends OxRuntimeException {
    public InvalidMongoDatabaseConfiguration(String msg) {
        super(msg);
    }
}