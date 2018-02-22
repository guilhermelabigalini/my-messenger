package my.messenger.androidclient.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(indices = {@Index(value = {"connectionId", "id"})})
public class ChatMessageDB {

    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * if inside group, connectionId is the group id
     * if from a user,  connectionId is the user id
     */
    @NonNull
    public String connectionId;

    /**if inside group, fromId is the id of the user that sent the message
     * if from a user, fromUserId is the user id
     */
    @NonNull
    public String fromUserId;

    public Date messageDt;

    public String body;

    // true if I sent , false I received
    public boolean sent;

    // 0 - user
    // 1 - group
    public int destinationType;
}
