package ox.engine.internal;

import ox.utils.logging.Logger;
import ox.utils.logging.Loggers;

import static java.lang.Thread.sleep;

public class LockRefresher implements Runnable {

    private final static Logger LOG = Loggers.getLogger(LockRefresher.class);
    private final LockHandler lockHandler;
    private final int interval;
    private final Lock lock;
    private boolean completed = false;

    public LockRefresher(
            LockHandler lockHandler,
            Lock lock,
            int interval) {
        this.lockHandler = lockHandler;
        this.interval = interval;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            while (!completed) {
                boolean result = lockHandler.refreshLock(lock);
                if (!result) {
                    LOG.warn("[Ox] Failed to refresh lock! Stopping the lock refresher...");
                    completed = true;
                }
                if (!completed) {
                    sleep(interval);
                }
            }
        } catch (InterruptedException e) {
            LOG.error("[Ox] Lock refresher was interrupted!", e);
        }
    }

    public void complete() {
        this.completed = true;
    }
}
