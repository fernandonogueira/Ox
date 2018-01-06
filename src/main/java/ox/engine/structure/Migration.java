package ox.engine.structure;

import ox.engine.exception.GenericMongoMigratorException;
import ox.engine.internal.MigrationEnvironment;

/**
 * This is a migration template file.
 *
 * Every migration file should implements this interface
 *
 * @author Fernando Nogueira
 * @since 4/11/14 10:23 PM
 */
public interface Migration {

    void up(MigrationEnvironment env) throws GenericMongoMigratorException;
    void down(MigrationEnvironment env) throws GenericMongoMigratorException;

}
