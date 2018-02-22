package my.messenger.androidclient.api;

/**
 * Created by guilherme on 1/5/2018.
 */

public class InvalidMyMessengerRequest extends Exception {
    public InvalidMyMessengerRequest(String message) {
        super(message);
    }

    public InvalidMyMessengerRequest(String message, Throwable cause) {
        super(message, cause);
    }
}
