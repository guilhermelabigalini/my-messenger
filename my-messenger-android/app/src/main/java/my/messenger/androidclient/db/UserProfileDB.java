package my.messenger.androidclient.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(indices = {@Index(value = "userName")})
public class UserProfileDB {

    @PrimaryKey
    @NonNull
    public String id;

    @NonNull
    public String userName;

    @Nullable
    public String lastMessage;

    @Nullable
    public Date lastMessageAt;

    @Nullable
    public int unreadCount;

    @Nullable
    public long lastReadMessageId;

    @Nullable
    public long lastReceivedMessageId;

    @Nullable
    public int type;

    @Nullable
    public boolean hidden;
}
