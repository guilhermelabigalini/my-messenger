package my.messenger.androidclient.api.model;

/**
 * Created by guilherme on 1/26/2018.
 */

public class StreamMessage {
    private StreamMessageType streamMessageType;
    private Message message;
    String tokenId;

    public StreamMessageType getStreamMessageType() {
        return streamMessageType;
    }

    public void setStreamMessageType(StreamMessageType streamMessageType) {
        this.streamMessageType = streamMessageType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
