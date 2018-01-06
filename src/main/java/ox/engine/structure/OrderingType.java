package ox.engine.structure;

/**
 * This is currently used to set if a index will
 * be created using ASC, DESC or Geospatial ordering.
 *
 * @author Fernando Nogueira
 * @since 4/14/14 11:19 AM
 */
public enum OrderingType {
    ASC, DESC, GEO_2DSPHERE;
}