package ox.integration.duplicate.migrations;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0002__create_index implements Migration {
    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction.createIndex("email_index")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .setCollection("test_collection")
        );
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
