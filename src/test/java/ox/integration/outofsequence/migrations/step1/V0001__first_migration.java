package ox.integration.outofsequence.migrations.step1;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0001__first_migration implements Migration {

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {
        env.execute(OxAction
                .createIndex("my_index1")
                .addAttribute("attr1", OrderingType.ASC)
                .setCollection("test_collection"));

    }

    @Override
    public void down(OxEnvironment env) {
    }

}
