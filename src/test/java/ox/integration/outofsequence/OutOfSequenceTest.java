package ox.integration.outofsequence;

import com.mongodb.MongoClient;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OutOfSequenceTest extends OxBaseContainerTest {

    @Test
    public void orderedMigrationsTest() throws InvalidMongoConfiguration {

        String migrationsPackage = "ox.integration.outofsequence.migrations";

        MongoClient mongo = getDefaultMongo();

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("oxOutOfSequenceTest")
                .scanPackage(migrationsPackage + ".step1")
                .build();

        Ox ox = Ox.setUp(config);

        Integer databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion)
                .withFailMessage("Database is initializing. It should have version=0")
                .isEqualTo(0);

        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(1);

        config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("oxOutOfSequenceTest")
                .scanPackage(migrationsPackage + ".step2")
                .build();

        ox = Ox.setUp(config);

        ox.up();
        databaseVersion = ox.databaseVersion();

        assertThat(databaseVersion).isEqualTo(3);

        config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("oxOutOfSequenceTest")
                .scanPackage(migrationsPackage + ".step3")
                .build();

        ox = Ox.setUp(config);

        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(3);

        MongoCollection<Document> collection = getDefaultMongo().getDatabase("oxOutOfSequenceTest").getCollection("test_collection");
        ListIndexesIterable<Document> indexes = collection.listIndexes();

        MongoCursor<Document> it = indexes.iterator();
        boolean found = false;
        while (it.hasNext()) {
            Document next = it.next();
            if (next.get("name").equals("my_index3")) {
                found = true;
            }
        }

        assertThat(found)
                .withFailMessage("Out of sequence migration should be executed either")
                .isTrue();
    }


}
