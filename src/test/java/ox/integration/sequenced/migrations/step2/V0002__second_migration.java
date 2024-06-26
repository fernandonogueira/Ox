package ox.integration.sequenced.migrations.step2;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0002__second_migration implements Migration {

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {
        env.execute(OxAction
                .createIndex("my_index2")
                .setCollection("test_col1")
                .addAttribute("attr2", OrderingType.DESC)
                .unique()
        );
    }

    @Override
    public void down(OxEnvironment env) {
    }
}
