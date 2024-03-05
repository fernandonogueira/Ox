package ox.engine.exception;

public class InvalidReadPreferenceException extends OxRuntimeException {
    public InvalidReadPreferenceException() {
        super("Invalid readPreference. Must be primary.");
    }
}
