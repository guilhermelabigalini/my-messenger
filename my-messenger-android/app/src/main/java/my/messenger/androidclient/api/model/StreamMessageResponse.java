package my.messenger.androidclient.api.model;

/**
 * Created by guilherme on 2/8/2018.
 */

public class StreamMessageResponse {
    public boolean ok;
    public UserSession userSession;
    public Message message;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
