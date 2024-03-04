package ox.migrationscollection;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
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

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("withoutSchemaMigrationsCollection")
                .scanPackage("ox.db.migrations")
                .disableMigrationCollectionCreation()
                .build();

        Ox ox = Ox.setUp(config);

        ox.databaseVersion();

    }
}
