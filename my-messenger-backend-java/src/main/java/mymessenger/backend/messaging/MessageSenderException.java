/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.messaging;

/**
 *
 * @author guilherme
 */
public class MessageSenderException extends Exception {

    public MessageSenderException(Throwable cause) {
        super(cause);
    }

    public MessageSenderException(String message) {
        super(message);
    }

    public MessageSenderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
