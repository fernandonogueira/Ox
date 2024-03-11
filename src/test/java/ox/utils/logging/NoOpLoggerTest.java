package ox.utils.logging;

import org.junit.Test;

public class NoOpLoggerTest {

    @Test
    public void testNoOpLogger() {
        NoOpLogger noOpLogger = new NoOpLogger("test");
        assert noOpLogger.name() == null;
    }
}
