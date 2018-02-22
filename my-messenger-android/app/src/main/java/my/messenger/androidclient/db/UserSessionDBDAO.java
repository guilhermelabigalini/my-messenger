package my.messenger.androidclient.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by guilherme on 1/6/2018.
 */

@Dao
public interface UserSessionDBDAO {

    @Query("SELECT * FROM UserSessionDB LIMIT 2")
    List<UserSessionDB> getSession();

    @Query("Delete FROM UserSessionDB")
    void truncate();

    @Insert
    void insert(UserSessionDB dbsession);
}
