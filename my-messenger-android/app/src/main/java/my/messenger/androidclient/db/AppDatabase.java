package my.messenger.androidclient.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(
        entities = {UserProfileDB.class, UserSessionDB.class, ChatMessageDB.class},
        version = 7,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDBDAO userProfileDBDao();

    public abstract UserSessionDBDAO userSessionDBDAODao();

    public abstract ChatMessageDBDAO chatMessageDBDAO();
}
