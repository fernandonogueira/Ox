package ox.utils.logging;

public record NoOpLogger(String name) implements Logger {
    @Override
    public String name() {
        return null;
    }
}
