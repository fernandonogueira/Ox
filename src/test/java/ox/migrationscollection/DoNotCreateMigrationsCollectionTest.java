package ox.migrationscollection;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.exception.CouldNotCreateCollectionException;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

public class DoNotCreateMigrationsCollectionTest extends OxBaseContainerTest {

    @Test(expected = CouldNotCreateCollectionException.class)
    public void shouldNotCreateMigrationsCollection() throws InvalidMongoConfiguration {

        MongoClient mongo = getDefaultMongo();
        mongo.getDatabase("withoutSchemaMigrationsCollection")
                .getCollection("someTest")
                .insertOne(new Document("test", true));

        Ox ox = Ox.setUp(
                mongo,
                "ox.db.migrations",
                "withoutSchemaMigrationsCollection",
                false);

        ox.databaseVersion();

    }
}
