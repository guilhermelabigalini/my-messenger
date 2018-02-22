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
public interface MessageReceiver {

    Subscriber subscribe(String userId, MessageConsumer consumer) throws MessageSenderException;
}
