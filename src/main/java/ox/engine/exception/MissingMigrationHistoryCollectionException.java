package ox.engine.exception;

public class MissingMigrationHistoryCollectionException extends RuntimeException {
    public MissingMigrationHistoryCollectionException(String message) {
        super(message);
    }
}
