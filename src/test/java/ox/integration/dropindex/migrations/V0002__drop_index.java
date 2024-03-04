package ox.integration.dropindex.migrations;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;

public class V0002__drop_index implements Migration {
    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction
                .removeIndex("email_index")
                .setCollection("drop_index_test"));
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
