package ox;

/**
 * @author Fernando Nogueira
 * @since 4/14/14 3:22 PM
 */
public final class Configuration {

    /**
     * Utils
     */
    private Configuration() {
    }

    public static final String MIGRATION_COLLECTION_VERSION_ATTRIBUTE = "version";
    public static final String MODULE_TAG = "[MONGO_MIGRATION]";
    public static final String SCHEMA_VERSION_COLLECTION_NAME = "migration_versions";
}
