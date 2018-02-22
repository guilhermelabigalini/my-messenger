package my.messenger.androidclient.services;

import my.messenger.androidclient.api.InvalidMyMessengerRequest;

/**
 * Created by guilherme on 1/7/2018.
 */

public class MyMessengerServiceException extends Exception {
    public MyMessengerServiceException(String message) {
        super(message);
    }

    public MyMessengerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
