package ox.engine.internal;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.exception.IndexAlreadyExistsException;
import ox.engine.exception.InvalidAttributeException;
import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CreateIndexAction extends OxAction {

    private static final Logger LOG = LoggerFactory.getLogger(CreateIndexAction.class);

    private Map<String, OrderingType> attributes = new LinkedHashMap<String, OrderingType>();

    private final String indexName;
    private boolean ifNotExists;
    /**
     * Unique index?
     */
    private boolean unique;
    /**
     * DropDups. Deprecated after MongoDB 3.0
     */
    private boolean dropDups;
    /**
     * Recreate the index if not equals?
     */
    private boolean recreateIfNotEquals;
    /**
     * True if this is a TTL index
     */
    private boolean ttlIndex;
    /**
     * Used if this TTL index is being created -> Time to expire the document
     */
    private long ttlIndexExpireAfterSeconds;

    public CreateIndexAction(String indexName) {
        this.indexName = indexName;
    }

    public java.util.Set<Map.Entry<String, OrderingType>> getAttributes() {
        return attributes.entrySet();
    }

    public String getIndexName() {
        return indexName;
    }

    public CreateIndexAction addAttribute(String attr, OrderingType o) throws InvalidAttributeException {

        if (attr == null) {
            throw new InvalidAttributeException("A Null attribute can't be added");
        }

        if (!attributes.containsKey(attr)) {
            attributes.put(attr, o);
        }

        return this;
    }

    /**
     * Set the collection that the index will be created
     *
     * @param collection the collection name
     */
    public CreateIndexAction setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    /**
     * This method sets a flag which indicates that the index will only be created if the index doesn't exists yet.
     * <p/>
     * Two tests are made to identify an already created index.
     * <pre>
     *     <ul>
     *         <li>Test1 - Verifies if the collection contains an index with the same name</li>
     *         <li>Test2 - Verifies if the collection contains an index with the same attributes, in the same order</li>
     *     </ul>
     * </pre>
     */
    public CreateIndexAction ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * This method sets a flag to indicate that the index must be configured to do not accept duplicated values.
     */
    public CreateIndexAction unique() {
        this.unique = true;
        return this;
    }

    /**
     * Marks this index as TTL ( http://docs.mongodb.org/manual/core/index-ttl/ )
     */
    public CreateIndexAction markAsTTL(long expireAfterSeconds) {
        this.ttlIndex = true;
        this.ttlIndexExpireAfterSeconds = expireAfterSeconds;
        return this;
    }

    /**
     * This method sets a flag to active the "dropDups" MongoDB CreateIndex Option.
     * <p/>
     * With this option activated, any existing duplicated values will be deleted by MongoDB automatically.
     * <p/>
     * If this option is not set and there are duplicated options, MongoDB will throw an exception trying to create the index.
     */
    public CreateIndexAction dropDups() {
        this.dropDups = true;
        return this;
    }

    /**
     * Setting this option, Ox will first verify if there is an existing index with the same name
     * and with different attributes or attributes order.
     * <p/>
     * If an existing index is found, MongoDB Java Migrator will first remove the index and create the new index.
     */
    public CreateIndexAction recreateIfNotEquals() {
        this.recreateIfNotEquals = true;
        return this;
    }

    @Override
    public String toString() {
        return "CreateIndexAction{" +
                "attributes=" + attributes +
                ", indexName=\'" + indexName + '\'' +
                "}";
    }

    @Override
    protected void validateAction() throws InvalidMigrateActionException {
        if (indexName == null || collection == null || collection.equals("")) {
            throw new InvalidMigrateActionException("Invalid Migrate action.");
        }

        if (attributes != null && attributes.size() > 1 && ttlIndex) {
            throw new InvalidMigrateActionException("TTL indexes can not have more than 1 attribute");
        }

    }

    @Override
    void runAction(MongoDBConnector mongoDBConnector, Mongo mongo, String databaseName) {

        boolean existsWithSameNameAndDifferentAttrs = mongoDBConnector
                .verifyIfHasSameNameAndDifferentAttributes(attributes, indexName, collection);

        if (existsWithSameNameAndDifferentAttrs && recreateIfNotEquals) {
            LOG.warn("[Ox] Index exists with different attrs. Removing to recreate...");
            mongoDBConnector.dropIndexByName(collection, indexName);
        }

        boolean doesItExists = mongoDBConnector.verifyIfIndexExists(attributes, indexName, collection);

        if (doesItExists) {
            if (ifNotExists) {
                LOG.warn("[Ox] Ignoring create index action. Index already exists and \"ifNotExists\" flag is set");
            } else {
                throw new IndexAlreadyExistsException("Index already exists. (Same name or same attributes)");
            }
        } else {
            BasicDBObject keys = parseAttributesToDBObject();
            BasicDBObject opts = generateCreateIndexOptions();
            mongoDBConnector.createIndex(collection, keys, opts);
        }

    }

    private BasicDBObject generateCreateIndexOptions() {
        BasicDBObject opts = new BasicDBObject();
        opts.append("background", true);
        opts.append("name", indexName);

        if (unique) {
            opts.append("unique", true);
            if (dropDups) {
                opts.append("dropDups", true);
            }
        }

        if (ttlIndex) {
            opts.append("expireAfterSeconds", ttlIndexExpireAfterSeconds);
        }

        return opts;
    }

    private BasicDBObject parseAttributesToDBObject() {
        Set<Map.Entry<String, OrderingType>> entries = attributes.entrySet();
        Iterator<Map.Entry<String, OrderingType>> it = entries.iterator();

        BasicDBObject keys = new BasicDBObject();
        while (it.hasNext()) {
            Map.Entry<String, OrderingType> current = it.next();

            if (OrderingType.ASC.equals(current.getValue())) {
                keys.append(current.getKey(), 1);
            } else if (OrderingType.DESC.equals(current.getValue())) {
                keys.append(current.getKey(), -1);
            } else if (OrderingType.GEO_2DSPHERE.equals(current.getValue())) {
                keys.append(current.getKey(), "2dsphere");
            } else {
                throw new InvalidAttributeException("An invalid attribute ordering was set. Please fix it");
            }
        }
        return keys;
    }

    @Override
    public String getCollection() {
        return collection;
    }

}
