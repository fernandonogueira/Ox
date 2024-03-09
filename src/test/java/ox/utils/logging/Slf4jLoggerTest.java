package ox.utils.logging;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Slf4jLoggerTest {

    @Test
    public void testSlf4jLogger() {
        Logger slf4jLogger = Loggers.getLogger(Slf4jLoggerTest.class);
        assertThat(slf4jLogger.name()).isEqualTo("ox.utils.logging.Slf4jLoggerTest");
    }

    @Test
    public void loggersTest() {
        Logger logger = Loggers.getLogger(Slf4jLoggerTest.class);
        assertThat(logger.isErrorEnabled()).isTrue();
        assertThat(logger.isInfoEnabled()).isTrue();
        assertThat(logger.isWarnEnabled()).isTrue();
        assertThat(logger.isDebugEnabled()).isTrue();
    }

}
