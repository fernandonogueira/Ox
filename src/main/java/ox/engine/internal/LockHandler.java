package ox.engine.internal;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.OxConfig;
import ox.engine.exception.NoLockCollectionException;
import ox.engine.structure.OrderingType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LockHandler {

    private static final String LOCK_NAME = "ox-migration-lock";

    private enum LockStatus {
        PROCESSING, DONE
    }

    private final Logger logger = LoggerFactory.getLogger(LockHandler.class);
    private final OxConfig oxConfig;

    public LockHandler(OxConfig oxConfig) {
        this.oxConfig = oxConfig;
    }

    private static Document dateDocument(String operator, ZonedDateTime date) {
        return new Document(operator, date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    private static Document generateRunDateQueryPart() {
        final Document query = new Document();
        query.put("status", LockStatus.DONE.name());
        query.put("lock_date", new Document("$lt", ZonedDateTime.now(Clock.systemUTC())));
        return query;
    }

    private static Document generateStatusProcessingAndHeartbeatNullQuery() {
        final Document query = new Document();
        query.put("status", LockStatus.PROCESSING.name());
        return query;
    }

    private static Bson generatePeekQuery(final long ttlSeconds) {
        return Filters.or(
                generateRunDateQueryPart(),
                generateStatusProcessingAndHeartbeatNullQuery());
    }

    public void ensureLockCollectionExists() {
        MongoClient mongo = oxConfig.mongo();
        MongoDatabase database = mongo.getDatabase(oxConfig.databaseName());

        findOrCreateLockCollection(database);
        findOrCreateLockIndex(database);
    }

    private Bson lockQuery(String lockName) {

        return Filters.or(
                Filters.and(
                        Filters.eq("lock_name", lockName),
                        Filters.eq("status", LockStatus.DONE.name())
                ),
                Filters.and(
                        Filters.eq("lock_name", lockName),
                        Filters.eq("status", LockStatus.PROCESSING.name()),
                        Filters.lt("expire_at", Date.from(ZonedDateTime.now(Clock.systemUTC()).toInstant()))
                ),
                Filters.and(
                        Filters.eq("lock_name", lockName),
                        Filters.eq("status", LockStatus.PROCESSING.name()),
                        Filters.eq("expire_at", null)
                )
        );
    }

    private Bson lockUpdate(long ttlSeconds) {
        String owner;
        try {
            owner = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            owner = "unknown" + UUID.randomUUID();
        }

        return Updates.combine(
                Updates.set("owner", owner),
                Updates.set("lock_name", "ox-migration-lock"),
                Updates.set("status", LockStatus.PROCESSING.name()),
                Updates.set("expire_at", Date.from(ZonedDateTime.now(Clock.systemUTC()).plusSeconds(ttlSeconds).toInstant())),
                Updates.set("lock_date", Date.from(ZonedDateTime.now(Clock.systemUTC()).toInstant())));
    }

    public Lock acquireLock() {

        MongoCollection<Document> collection = oxConfig.mongo().getDatabase(oxConfig.databaseName()).getCollection(oxConfig.collectionsConfig().lockCollectionName());

        Bson query = lockQuery("ox-migration-lock");
        Bson update = lockUpdate(oxConfig.extras().lockTTLSeconds());

        FindOneAndUpdateOptions opts = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(false);
        Document lockDoc = collection.findOneAndUpdate(query, update, opts);
        Lock lock = Lock.fromDocument(lockDoc);

        return lock;
    }

    public void releaseLock(Lock lock) {
        MongoCollection<Document> collection = oxConfig.mongo().getDatabase(oxConfig.databaseName()).getCollection(oxConfig.collectionsConfig().lockCollectionName());
        Document query = new Document("owner", lock.owner())
                .append("status", LockStatus.PROCESSING.name())
                .append("lock_date", dateDocument("$eq", lock.lockDate()));
        Document update = new Document("$set", new Document("status", LockStatus.DONE.name()));
        collection.updateOne(query, update);
    }

    private void findOrCreateLockIndex(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(oxConfig.collectionsConfig().lockCollectionName());
        List<Document> indexes = collection.listIndexes().into(new ArrayList<>());
        boolean indexExists = indexes.stream().anyMatch(index -> index.get("name").equals("lock_date_1_status_1"));

        if (!indexExists) {
            logger.info("[Ox] Lock index does not exist. Creating: lock_date_1_status_1");

            CreateIndexAction idxDef = OxAction.createIndex("idx_lock_name_1")
                    .addAttribute("lock_name", OrderingType.ASC)
                    .unique()
                    .setCollection(oxConfig.collectionsConfig().lockCollectionName())
                    .recreateIfNotEquals();

            try {
                collection.createIndex(idxDef.parseAttributesToDBObject(), idxDef.generateCreateIndexOptions());
            } catch (RuntimeException e) {
                logger.error("Error creating lock index", e);
                throw new NoLockCollectionException("Error creating lock index");
            }
        } else {
            logger.info("[Ox] Lock indexes exist.");
        }

        try {
            collection.insertOne(new Document("lock_name", LOCK_NAME).append("status", LockStatus.DONE.name()));
        } catch (Exception e) {
            logger.debug("[Ox] ensuring lock document exists", e);
        }

    }

    private void findOrCreateLockCollection(MongoDatabase database) {
        List<String> collectionNames = database.listCollectionNames().into(new ArrayList<>());
        if (!collectionNames.contains(oxConfig.collectionsConfig().lockCollectionName())) {
            if (oxConfig.collectionsConfig().createLockCollection()) {
                logger.info("[Ox] Lock collection does not exist. Creating: " + oxConfig.collectionsConfig().lockCollectionName());
                database.createCollection(oxConfig.collectionsConfig().lockCollectionName());
            } else {
                throw new NoLockCollectionException("Lock collection does not exist and createLockCollection is set to false. Collection name: " + oxConfig.collectionsConfig().lockCollectionName());
            }
        } else {
            logger.info("[Ox] Lock collection exists: " + oxConfig.collectionsConfig().lockCollectionName());
        }
    }


}
