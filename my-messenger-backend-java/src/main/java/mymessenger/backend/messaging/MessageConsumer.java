/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.messaging;

import mymessenger.backend.model.messaging.Message;

/**
 *
 * @author guilherme
 */
public interface MessageConsumer {
    
    void handle(Message msg) throws Exception;
    
    boolean canHandle();
}
