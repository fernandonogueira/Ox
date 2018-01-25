package ox.ordered.migrations.step1;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0001__first_migration implements Migration {

    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction
                .createIndex("my_index1")
                .setCollection("test_col1")
                .addAttribute("attr1", OrderingType.ASC)
                .unique().dropDups());
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
