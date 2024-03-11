package ox.engine.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;

@RunWith(MockitoJUnitRunner.class)
public class ValidateRemoveIndexActionTest {

    /**
     * No collection set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexAction() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("someIndex")
                .validateAction();
    }

    /**
     * No index name set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexActionWithoutIndexName() throws InvalidMigrateActionException {
        OxAction
                .removeIndex(null)
                .validateAction();
    }

    /**
     * Happy day!
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void validateRemoveIndexActionHappyDay() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("correctIndexName")
                .setCollection("collectionSet")
                .validateAction();
    }

}
