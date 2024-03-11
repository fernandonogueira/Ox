package ox.integration.concurrency.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;

public class V0001_SlowMigration1 implements Migration {

    private static final Logger LOG = LoggerFactory.getLogger(V0001_SlowMigration1.class);

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {
        try {
            LOG.info("Starting slow migration 1");
            Thread.sleep(3000);
            LOG.info("Finishing slow migration 1");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void down(OxEnvironment env) {
    }
}
