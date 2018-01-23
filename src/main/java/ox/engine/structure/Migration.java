package ox.engine.structure;

import ox.engine.exception.OxException;
import ox.engine.internal.OxEnvironment;

/**
 * This is a migration template file.
 * <p>
 * Every migration file should implements this interface
 *
 * @author Fernando Nogueira
 * @since 4/11/14 10:23 PM
 */
public interface Migration {

    void up(OxEnvironment env) throws OxException;

    void down(OxEnvironment env) throws OxException;

}
