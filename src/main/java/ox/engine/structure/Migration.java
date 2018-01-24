package ox.engine.structure;

import ox.engine.exception.OxException;
import ox.engine.internal.OxEnvironment;

/**
 * This is a migration template file.
 * <p>
 * Every migration file should implements this interface
 */
public interface Migration {

    void up(OxEnvironment env) throws OxException;

    void down(OxEnvironment env) throws OxException;

}
