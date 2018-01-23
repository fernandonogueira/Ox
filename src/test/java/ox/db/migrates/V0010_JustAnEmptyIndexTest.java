package ox.db.migrates;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;

/**
 * @author Fernando Nogueira
 * @since 4/11/14 10:23 PM
 */
public class V0010_JustAnEmptyIndexTest implements Migration {

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {
    }

    @Override
    public void down(OxEnvironment env) {
    }
}
