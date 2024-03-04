package ox.engine.internal;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;
import ox.utils.TestUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateIndexTest {


    /**
     * Tries to create a index an invalid index
     * (Same name)
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void createAnInvalidIndexTest() throws InvalidMigrateActionException {

        OxEnvironment env = new OxEnvironment();

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .execute(env);

    }

    /**
     * Tries to create an invalid TTL index (with more than 1 attribute)
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void createAnInvalidTTLIndex() throws InvalidMigrateActionException {

        OxEnvironment env = new OxEnvironment();
        OxAction
                .createIndex("myIndex")
                .setCollection("myRandomCollection")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC)
                .markAsTTL(TimeUnit.DAYS.toSeconds(90))
                .execute(env);
    }

}
