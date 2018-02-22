package my.messenger.androidclient;

import android.content.Context;

public class MyMessenger extends android.app.Application {

    private static MyMessenger mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext.getApplicationContext();
    }
}
