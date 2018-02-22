/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.model.users;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

/**
 *
 * @author guilherme
 */
public class UserSession {

    @Id
    private String id;
    private LocalDateTime createdAt;
    private String userId;

    public UserSession(String sessionId, LocalDateTime createdAt, String userId) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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
