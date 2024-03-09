package ox.utils.logging;

import org.slf4j.LoggerFactory;

public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger delegate;

    Slf4jLogger(final String name) {
        this.delegate = LoggerFactory.getLogger(name);
    }

    @Override
    public String name() {
        return delegate.getName();
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        delegate.warn(msg);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        delegate.warn(msg, t);
    }

    @Override
    public void warn(String msg, Object... objects) {
        delegate.warn(msg, objects);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        delegate.error(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        delegate.info(msg, t);
    }

    @Override
    public void info(String msg, Object... objects) {
        this.delegate.info(msg, objects);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.delegate.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        delegate.debug(msg);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        delegate.debug(msg, t);
    }


}
