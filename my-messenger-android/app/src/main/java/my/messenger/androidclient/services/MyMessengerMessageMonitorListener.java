package my.messenger.androidclient.services;

import my.messenger.androidclient.api.model.Message;

public interface MyMessengerMessageMonitorListener {
    void handleReceivedMessage(Message m);
}
