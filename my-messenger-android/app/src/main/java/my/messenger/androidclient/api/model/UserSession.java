package my.messenger.androidclient.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by guilherme on 1/5/2018.
 */

public class UserSession {
    private String id;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date createdAt;
    private String userId;

    public UserSession(String sessionId, Date createdAt, String userId) {
        this.id = sessionId;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public UserSession() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* TODO: fix backend, format should be - "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" http://www.sdfonlinetester.info/#
     the service is respoding "createdAt": "2018-01-14T21:08:00.9856249+00:00",
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserSession{" + "id=" + id + ", createdAt=" + createdAt + ", userId=" + userId + '}';
    }
}
