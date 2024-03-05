package ox.utils;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.structure.OrderingType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IndexUtils {

    private static final Logger LOG = LoggerFactory.getLogger(IndexUtils.class);

    /**
     * Identify index attributes and ordering from a DBObject
     * <p/>
     * This DBObject is the DBObject that contains the info from an index retrieved from Database.
     *
     * @param current The DBObject retrieved from the IndexInfo List.
     * @return A map containing the attributes and ordering
     */
    public static Map<String, OrderingType> identifyIndexAttributesAndOrdering(Document current) {
        Map<String, OrderingType> existingAttributes = new LinkedHashMap<>();
        Document indexAttrs = (Document) current.get("key");

        if (indexAttrs != null) {
            Set<String> attrKeySet = indexAttrs.keySet();

            for (String currentAttr : attrKeySet) {
                Object attrOrdering = indexAttrs.get(currentAttr);
                if (attrOrdering instanceof Integer) {
                    existingAttributes.put(currentAttr, attrOrdering.equals(1) ? OrderingType.ASC : OrderingType.DESC);
                } else if (attrOrdering instanceof String) {
                    existingAttributes.put(currentAttr, OrderingType.GEO_2DSPHERE);
                }
            }
        }
        return existingAttributes;
    }

    public static boolean verifyIfHasSameAttributesWithSameOrder(Map<String, OrderingType> indexAttributes, Document current) {
        if (indexAttributes != null) {
            Map<String, OrderingType> existingAttributes = identifyIndexAttributesAndOrdering(current);

            if (CollectionUtils.isMapOrderlyEquals(indexAttributes, existingAttributes)) {
                LOG.info("[Ox] Index already exists. (Same attributes. Same Order); {}", existingAttributes);
                return true;
            }
        }
        return false;
    }

}
