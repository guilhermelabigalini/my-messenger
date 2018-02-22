package my.messenger.androidclient.services;

import my.messenger.androidclient.db.ChatMessageDB;

/**
 * Created by guilherme on 2/2/2018.
 */

public interface MyMessengerMessageListener {

    void onSentOrReceivedMessage(ChatMessageDB m);

}
