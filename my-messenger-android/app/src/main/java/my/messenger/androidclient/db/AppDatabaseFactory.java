package my.messenger.androidclient.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import my.messenger.androidclient.MyMessenger;

/**
 * Created by guilherme on 1/6/2018.
 */

public final class AppDatabaseFactory {

    private static AppDatabase mInstance;

    public static AppDatabase create() {

        if (mInstance == null) {
            mInstance = Room.databaseBuilder(MyMessenger.getContext(),
                    AppDatabase.class, "my_messenger_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }

    public static void detroy(){
        mInstance = null;
    }

}
