package ox.integration.actions.migrations;

import ox.engine.exception.OxException;
import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;

public class V0001__remove_collection implements Migration {

    @Override
    public void up(OxEnvironment env) throws OxException {
        env.execute(OxAction.removeCollection("collection1"));
    }

    @Override
    public void down(OxEnvironment env) throws OxException {

    }
}
