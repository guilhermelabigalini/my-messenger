package my.messenger.androidclient.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface UserProfileDBDAO {

    @Query("SELECT * FROM UserProfileDB")
    List<UserProfileDB> getAll();

    @Query("SELECT * FROM UserProfileDB WHERE userName = :un LIMIT 1")
    UserProfileDB findByUsername(String un);

    @Query("SELECT * FROM UserProfileDB WHERE id = :id LIMIT 1")
    UserProfileDB findByUserId(String id);

    @Query("Delete FROM UserProfileDB")
    void truncate();

    @Insert
    void insert(UserProfileDB user);

    @Query("UPDATE UserProfileDB SET lastMessageAt = :at, lastMessage = :body, unreadCount = :newUnreadCount, lastReceivedMessageId = :lastReceivedMessageId  WHERE id = :userId")
    int setLastMessage(String userId, String body, Date at, int newUnreadCount, long lastReceivedMessageId);

    @Query("UPDATE UserProfileDB SET unreadCount = 0, lastReadMessageId = :lastReadMessageId WHERE id = :userId")
    int markMessagesAsRead(String userId, long lastReadMessageId);

    @Query("UPDATE UserProfileDB SET hidden = :b WHERE id = :userId")
    void setHidden(String userId, boolean b);

    @Query("DELETE FROM UserProfileDB WHERE id = :userId")
    void delete(String userId);
}
