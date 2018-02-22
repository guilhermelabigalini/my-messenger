/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.model.messaging;

/**
 *
 * @author guilherme
 */
public class TransmittedMessage {

    private Destination to;
    private MessageType type;
    private String body;

    public TransmittedMessage() {
    }

    public TransmittedMessage(Destination to, MessageType type, String body) {
        this.to = to;
        this.type = type;
        this.body = body;
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
    
    
}
