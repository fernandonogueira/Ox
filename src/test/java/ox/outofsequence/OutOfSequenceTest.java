package ox.outofsequence;

import com.mongodb.MongoClient;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.Test;
import ox.test.base.OxBaseContainerTest;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class OutOfSequenceTest extends OxBaseContainerTest {


    @Test
    public void orderedMigrationsTest() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();
        Ox ox = Ox.setUp(
                mongo,
                "ox.outofsequence.migrations.step1",
                "oxOutOfSequenceTest",
                true);

        Integer databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion)
                .withFailMessage("Database is initializing. It should have version=0")
                .isEqualTo(0);

        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(1);

        ox = Ox.setUp(
                mongo,
                "ox.outofsequence.migrations.step2",
                "oxOutOfSequenceTest",
                true);

        ox.up();
        databaseVersion = ox.databaseVersion();

        assertThat(databaseVersion).isEqualTo(3);

        ox = Ox.setUp(
                mongo,
                "ox.outofsequence.migrations.step3",
                "oxOutOfSequenceTest",
                true);

        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(3);

        MongoCollection<Document> collection = getDefaultMongo().getDatabase("oxOutOfSequenceTest").getCollection("test_collection");
        ListIndexesIterable<Document> indexes = collection.listIndexes();

        MongoCursor<Document> it = indexes.iterator();
        boolean found = false;
        while(it.hasNext()) {
            Document next = it.next();
            if (next.get("name").equals("my_index3")) {
                found = true;
            }
        }

        assertThat(found)
                .withFailMessage("Out of sequence migration should be executed either")
                .isTrue();
        System.out.println(indexes);

    }


}
