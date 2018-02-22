/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.ws;

import mymessenger.backend.model.messaging.Message;

/**
 *
 * @author guilherme
 */
public class StreamMessage {
    
    private StreamMessageType streamMessageType;
    
    private Message message;
    
    private String tokenId;

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
