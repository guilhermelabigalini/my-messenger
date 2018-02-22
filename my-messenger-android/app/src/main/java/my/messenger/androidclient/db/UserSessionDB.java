package my.messenger.androidclient.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by guilherme on 1/5/2018.
 */

@Entity(indices = {@Index("userId")})
public class UserSessionDB {

    @PrimaryKey
    @NonNull
    public String id;

    public Date createdAt;

    @NonNull
    public String userId;

    @NonNull
    public String userName;
}
