package ox.integration.ttl.migrations;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0001__create_index implements Migration {

    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction
                .createIndex("ttl_index")
                .addAttribute("createdAt", OrderingType.ASC)
                .setCollection("ttl_collection")
                .markAsTTL(600)
        );
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
