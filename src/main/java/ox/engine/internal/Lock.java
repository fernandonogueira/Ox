package ox.engine.internal;

import org.bson.Document;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record Lock(
        String owner,
        String status,
        ZonedDateTime lockDate,
        ZonedDateTime expireAt
) {
    static Lock fromDocument(Document document) {
        if (document == null) {
            return null;
        }
        return new Lock(
                document.getString("owner"),
                document.getString("status"),
                document.getDate("lock_date").toInstant().atZone(ZoneId.systemDefault()),
                document.getDate("expire_at").toInstant().atZone(ZoneId.systemDefault())
        );
    }
}
