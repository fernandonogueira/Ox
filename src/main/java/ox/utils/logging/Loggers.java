package ox.utils.logging;

public final class Loggers {

    private static final boolean USE_SLF4J = shouldUseSlf4j();

    private Loggers() {
    }

    public static Logger getLogger(Class<?> clazz) {
        if (USE_SLF4J) {
            return new Slf4jLogger(clazz.getName());
        } else {
            return new NoOpLogger(clazz.getName());
        }
    }

    public static boolean shouldUseSlf4j() {
        try {
            Class.forName("org.slf4j.Logger");
            return true;
        } catch (ClassNotFoundException e) {
            java.util.logging.Logger.getLogger("ox")
                    .warning( "SLF4J not found. Logging is disabled. Please add SLF4J to your classpath.");
            return false;
        }
    }

}
