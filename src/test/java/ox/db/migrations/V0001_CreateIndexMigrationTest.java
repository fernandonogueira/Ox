package ox.db.migrations;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0001_CreateIndexMigrationTest implements Migration {

    @Override
    public void up(OxEnvironment env) throws InvalidMigrateActionException {

        OxAction
                .createIndex("myIndex")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .execute(env);

        OxAction
                .createIndex("myIndex2")
                .setCollection("myCollection")
                .addAttribute("attr2", OrderingType.DESC)
                .addAttribute("attr3", OrderingType.ASC)
                .execute(env);

    }

    @Override
    public void down(OxEnvironment env) {
    }
}
