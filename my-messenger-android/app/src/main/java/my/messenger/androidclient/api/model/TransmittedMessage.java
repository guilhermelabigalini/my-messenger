package my.messenger.androidclient.api.model;

/**
 * Created by guilherme on 1/26/2018.
 */

public class TransmittedMessage {
    private Destination to;
    private MessageType type;
    private String body;

    public Destination getTo() {
        return to;
    }

    public void setTo(Destination to) {
        this.to = to;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "TransmittedMessage{" +
                "to=" + to +
                ", type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
