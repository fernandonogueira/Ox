package ox.utils.logging;

public interface Logger {

    String name();

    default void warn(String msg) {
    }

    default void warn(String msg, Throwable t) {
    }

    default void warn(String msg, Object... objects) {
    }

    default boolean isErrorEnabled() {
        return false;
    }

    default void error(String msg) {
    }

    default void error(String msg, Throwable t) {
    }

    default boolean isInfoEnabled() {
        return false;
    }

    default void info(String msg) {
    }

    default void info(String msg, Throwable t) {
    }

    default void info(String msg, Object... objects) {
    }

    default void debug(String msg) {
    }

    default void debug(String msg, Throwable t) {
    }


}
