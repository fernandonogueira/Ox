package ox.engine.internal;

import org.bson.Document;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Lock {

    private final String owner;
    private final String status;
    private final ZonedDateTime lockDate;
    private ZonedDateTime expireAt;

    public Lock(String owner, String status, ZonedDateTime lockDate, ZonedDateTime expireAt) {
        this.owner = owner;
        this.status = status;
        this.lockDate = lockDate;
        this.expireAt = expireAt;
    }

    public String owner() {
        return owner;
    }

    public String status() {
        return status;
    }

    public ZonedDateTime lockDate() {
        return lockDate;
    }

    public ZonedDateTime expireAt() {
        return expireAt;
    }

    public void setExpireAt(ZonedDateTime expireAt) {
        this.expireAt = expireAt;
    }

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
