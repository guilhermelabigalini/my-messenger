package my.messenger.androidclient.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by guilherme on 1/27/2018.
 */

@Dao
public interface ChatMessageDBDAO {
    @Query("SELECT * FROM ChatMessageDB WHERE connectionId = :connectionId AND id < :olderThanId ORDER BY id DESC LIMIT :limit ")
    List<ChatMessageDB> getMessagesOldThan(String connectionId, long olderThanId, int limit);

    @Query("SELECT * FROM ChatMessageDB WHERE connectionId = :connectionId ORDER BY id DESC LIMIT :limit ")
    List<ChatMessageDB> getFromUserId(String connectionId, int limit);

    @Query("SELECT * FROM ChatMessageDB WHERE id <= :beforeId AND connectionId = :connectionId ORDER BY id DESC LIMIT :limit ")
    List<ChatMessageDB> getFromUserId(String connectionId, long beforeId, int limit);

    @Query("SELECT * FROM ChatMessageDB WHERE id > :fromId AND connectionId = :connectionId ORDER BY id DESC ")
    List<ChatMessageDB> getFromUserIdFrom(String connectionId, long fromId);

    @Query("Delete FROM ChatMessageDB Where connectionId = :connectionId  ")
    void deleteFromUserId(String connectionId);

    @Query("Delete FROM ChatMessageDB")
    void truncate();

    @Insert
    long insert(ChatMessageDB chatMessageDB);
}
