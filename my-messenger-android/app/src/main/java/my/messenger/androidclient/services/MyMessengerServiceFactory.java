package my.messenger.androidclient.services;

import android.content.Context;

public final class MyMessengerServiceFactory {

    private static MyMessengerService mInstance;

    public static MyMessengerService getInstance() {
        if (mInstance == null) {
            mInstance = new MyMessengerService();
        }
        return mInstance;
    }
}
