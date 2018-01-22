package ox.engine.internal.resources;

import ox.engine.structure.Migration;

/**
 * Every migration file will be transformed
 * in a ResolvedMigration before execution.
 *
 * @author Fernando Nogueira
 * @since 4/16/14 10:26 AM
 */
public class ResolvedMigration {

    private Migration migrate;
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Migration getMigrate() {
        return migrate;
    }

    public void setMigrate(Migration migrate) {
        this.migrate = migrate;
    }

    @Override
    public String toString() {
        return "ResolvedMigration{" +
                "migrate=" + migrate +
                ", version=" + version +
                '}';
    }
}