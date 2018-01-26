package ox.integration.outofsequence.migrations.step3;

import ox.engine.internal.OxAction;
import ox.engine.internal.OxEnvironment;
import ox.engine.structure.Migration;
import ox.engine.structure.OrderingType;

public class V0002__third_migration implements Migration {

    @Override
    public void up(OxEnvironment env) {
        env.execute(OxAction.createIndex("my_index3")
                .addAttribute("attr3", OrderingType.ASC)
                .setCollection("test_collection"));
    }

    @Override
    public void down(OxEnvironment env) {
    }

}
