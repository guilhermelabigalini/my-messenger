/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.model.messaging;

import java.time.LocalDateTime;


/**
 *
 * @author guilherme
 */
public class Message {

    private String fromUserId;
    private Destination to;
    private MessageType type;
    private String body;
    private LocalDateTime sentAt;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

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

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Message{" + "fromUserId=" + fromUserId + ", to=" + to + ", type=" + type + ", body=" + body + ", sentAt=" + sentAt + '}';
    }
    
    
}
