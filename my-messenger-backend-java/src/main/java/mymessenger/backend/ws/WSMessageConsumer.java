/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.ws;

import mymessenger.backend.encoding.JsonUtil;
import mymessenger.backend.messaging.MessageConsumer;
import mymessenger.backend.model.messaging.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author guilherme
 */
public class WSMessageConsumer implements MessageConsumer {

    private final WebSocketSession session;

    public WSMessageConsumer(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void handle(Message msg) throws Exception {
        StreamMessageResponse response = StreamMessageResponse.message(msg);
        session.sendMessage(new TextMessage(JsonUtil.toString(response)));
    }

    @Override
    public boolean canHandle() {
        return session.isOpen();
    }
    
}
