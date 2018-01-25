package ox.engine.internal;

import org.junit.Test;
import ox.engine.exception.InvalidMigrateActionException;

public class ValidateRemoveCollectionActionTest {

    @Test
    public void removeCollectionShouldBeValidTest() throws InvalidMigrateActionException {
        OxAction action = OxAction
                .removeCollection("myCollection");

        action.validateAction();
    }

    @Test(expected = InvalidMigrateActionException.class)
    public void removeCollectionWithoutCollectionShouldThrowErrorTest() throws InvalidMigrateActionException {
        OxAction action = OxAction
                .removeCollection(null);

        action.validateAction();
    }

}
