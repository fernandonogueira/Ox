package ox.integration.dropindex.migrations;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0001__create_index implements Migration {

    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction
                .createIndex("email_index")
                .addAttribute("email", OrderingType.ASC)
                .setCollection("drop_index_test"));
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
