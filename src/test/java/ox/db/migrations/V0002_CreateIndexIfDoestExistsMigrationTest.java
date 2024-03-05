package ox.db.migrations;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0002_CreateIndexIfDoestExistsMigrationTest implements Migration {

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {

        env.execute(OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
        );

        env.execute(OxAction
                .createIndex("myIndex2")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr2", OrderingType.DESC)
                .addAttribute("attr3", OrderingType.ASC)
        );

    }

    @Override
    public void down(OxEnvironment env) {
    }
}
