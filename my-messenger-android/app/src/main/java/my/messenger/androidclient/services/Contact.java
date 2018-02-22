package my.messenger.androidclient.services;

import java.io.Serializable;
import java.util.Date;

import my.messenger.androidclient.api.model.UserProfile;

public class Contact {
    private boolean hidden;
    private UserProfile userProfile;
    private String lastMessage;
    private Date lastMessageReceivedAt;
    private int unreadCount;
    private long lastReceivedMessageId;
    private long lastReadMessageId;
    public int type;

    public Contact(UserProfile userProfile,
                   String lastMessage,
                   Date lastMessageReceivedAt,
                   int unreadCount,
                   long lastReceivedMessageId,
                   long lastReadMessageId,
                   int type,
                   boolean hidden) {
        this.userProfile = userProfile;
        this.lastMessage = lastMessage;
        this.lastMessageReceivedAt = lastMessageReceivedAt;
        this.unreadCount = unreadCount;
        this.lastReceivedMessageId = lastReceivedMessageId;
        this.lastReadMessageId = lastReadMessageId;
        this.type = type;
        this.hidden = hidden;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile id) {
        this.userProfile = id;
    }

    public String getLastMessageBody() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage, Date at, long lastReceivedMessageId) {
        this.lastMessage = lastMessage;
        this.lastMessageReceivedAt = at;
        this.lastReceivedMessageId = lastReceivedMessageId;
    }

    public Date getLastMessageReceivedAt() {
        return lastMessageReceivedAt;
    }

    public long getLastReceivedMessageId() {
        return lastReceivedMessageId;
    }

    public long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "userProfile=" + userProfile +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageReceivedAt=" + lastMessageReceivedAt +
                '}';
    }
}
